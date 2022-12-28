package com.prajekpro.api.dto;


import com.fasterxml.jackson.annotation.*;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.enums.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.utility.*;
import lombok.*;

import java.text.*;
import java.util.*;
import java.util.stream.*;

import static com.safalyatech.common.utility.CheckUtil.hasValue;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentBookingDTO {

    private AppointmentState appointmentState;
    private Long appointmentDetailsId;

    private Long proId;
    private String proUserId;
    private String proName;
    private String proAddr;
    private String proCntcNo;
    private String proEmail;
    private String proVatNo;

    private String customerId;
    private String customerName;
    private String customerCntcNo;

    private String userId;

    private Long userAddressDetailsId;
    private UserDeliveryAddressDTO userAddressDetails;
    private Long cancellationTime;
    private String cancellationTimeUnit;
    private Long cancellationTimeUnitId;
    private Long cancellationFees;
    private String cancellationFeesUnit;
    private Long cancellationFeesUnitId;
    private AppointmentInvoiceDTO invoiceDetails;
    private List<AppointmentServicesDTO> appointmentServices;
    private List<AppointmentOtherServicesDTO> appointmentOtherServices = new ArrayList<>();
    private Double subTotal = 0d;
    private Double grandTotal = 0d;
    private List<TaxConfigDTO> applicableTaxes = new ArrayList<>();
    private List<InvoiceProducts> invoiceProducts = new ArrayList<>();
    List<ApptDocsDTO> apptDocs = new ArrayList<>();


    public AppointmentBookingDTO(AppointmentDetails appointmentDetails) {
        this.appointmentState = appointmentDetails.getState();
        this.appointmentDetailsId = appointmentDetails.getId();

        ProDetails bookedFor = appointmentDetails.getBookedFor();
        this.proId = bookedFor.getId();
        this.proUserId = bookedFor.getUserDetails().getUserId();
        this.proVatNo = hasValue(bookedFor.getVatNo()) ? bookedFor.getVatNo() : "NA";

        Users userDetails = bookedFor.getUserDetails();
        this.proName = userDetails.getFullName();
        this.userId = userDetails.getUserId();
        this.proAddr = userDetails.getLocationText();
        this.proCntcNo = userDetails.getCntcNo();
        this.proEmail = userDetails.getEmailId();

        this.customerId = appointmentDetails.getBookedBy().getUserId();
        this.customerName = appointmentDetails.getBookedBy().getFullName();
        this.customerCntcNo = appointmentDetails.getBookedBy().getCntcNo();

        if (hasValue(appointmentDetails.getUserDeliveryAddress())) {
            UserDeliveryAddress userDeliveryAddress = appointmentDetails.getUserDeliveryAddress();
            this.userAddressDetails = new UserDeliveryAddressDTO(userDeliveryAddress);
        }

        if (hasValue(appointmentDetails.getAppointmentInvoice())) {
            this.invoiceDetails = new AppointmentInvoiceDTO(appointmentDetails.getAppointmentInvoice());
        }

        List<AppointmentRequestedServices> appointmentRequestedServices = appointmentDetails.getAppointmentRequestedServices();
        this.appointmentServices = new ArrayList<>();
        for (AppointmentRequestedServices service : appointmentRequestedServices) {
            if (service.getActiveStatus() == ActiveStatus.ACTIVE.value()) {
                //get Appointment Service List
                appointmentServices.add(new AppointmentServicesDTO(service));

                //to get total cost
                List<AppointmentRequestedServiceCategories> appCategory = service.getAppointmentRequestedServiceCategories();
                for (AppointmentRequestedServiceCategories category : appCategory) {
                    if (category.getActiveStatus() == ActiveStatus.ACTIVE.value()) {
                        List<AppointmentRequestedServiceSubCategories> appSubCategory = category.getAppointmentRequestedServiceSubCategories();
                        for (AppointmentRequestedServiceSubCategories subCategory : appSubCategory) {
                            if (subCategory.getActiveStatus() == ActiveStatus.ACTIVE.value()) {
                                Float prodTotal = ((subCategory.getRequestedPrice()) * (subCategory.getRequestedQty()));
                                this.subTotal = subTotal + prodTotal;
                                invoiceProducts.add(
                                        new InvoiceProducts(
                                                subCategory.getServiceItemSubCategory().getItemSubCategoryName(),
                                                Long.toString(subCategory.getRequestedQty()),
                                                Float.toString(subCategory.getRequestedPrice()),
                                                Float.toString(prodTotal)
                                        ));
                            }

                        }
                    }

                }

            }
        }

        if (hasValue(appointmentDetails.getAppointmentOtherServices())) {
            for (AppointmentOtherServices otherServices : appointmentDetails.getAppointmentOtherServices()) {
                if (otherServices.getActiveStatus() == ActiveStatus.ACTIVE.value()) {
                    AppointmentOtherServicesDTO appointmentOtherServicesDTO = new AppointmentOtherServicesDTO(otherServices);
                    appointmentOtherServices.add(appointmentOtherServicesDTO);
                    Float prodTotal = (otherServices.getUnitPrice() * otherServices.getReqQuantity());
                    this.subTotal = this.subTotal + prodTotal;
                    invoiceProducts.add(
                            new InvoiceProducts(
                                    otherServices.getServiceName(),
                                    Long.toString(otherServices.getReqQuantity()),
                                    Float.toString(otherServices.getUnitPrice()),
                                    Float.toString(prodTotal)
                            ));
                }
            }
        }
    }

    @JsonIgnore
    public InvoiceDtl getInvoiceDtl() {
        InvoiceDtl invoiceDtl = new InvoiceDtl();

        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy");

        invoiceDtl.setInvoiceType("APPOINTMENT");
        invoiceDtl.setInvoiceNo(invoiceDetails.getInvoiceNo());

        cal.setTimeInMillis(invoiceDetails.getInvoiceTs());
        invoiceDtl.setInvoiceDt(df.format(cal.getTime()));

        invoiceDtl.setInvoiceCurr("PHP");

        invoiceDtl.setProNm(proName);
        invoiceDtl.setProAddr(proAddr);
        invoiceDtl.setProCntcNo(proCntcNo);
        invoiceDtl.setProEmail(proEmail);
        invoiceDtl.setProVatNo(proVatNo);
        invoiceDtl.setProCd(Long.toString(proId));

        invoiceDtl.setReceiverNm(customerName);
        invoiceDtl.setReceiverAddr(userAddressDetails.getAddressString());
        invoiceDtl.setReceiverCntcNo(customerCntcNo);

        invoiceDtl.setProducts(invoiceProducts);

        invoiceDtl.setSubTotal(Double.toString(subTotal));
        invoiceDtl.setDiscount("0");
        invoiceDtl.setTaxableValue(Double.toString(subTotal));
        invoiceDtl.setTaxes(applicableTaxes.stream().map(t -> new DoubleValueClass(t.getLabel(), Float.toString(t.getTaxAmount()))).collect(Collectors.toList()));
        invoiceDtl.setTotalInvoiceValueInFig(Double.toString(grandTotal));
        invoiceDtl.setTotalInvoiceValueInWords(CommonUtility.convertNumberToWord(grandTotal.longValue()));
        return invoiceDtl;
    }
}
