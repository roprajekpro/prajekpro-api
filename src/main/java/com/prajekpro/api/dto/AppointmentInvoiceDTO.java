package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentInvoice;
import com.safalyatech.common.utility.CommonUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentInvoiceDTO {

    private Long invoiceId;
    private String invoiceNo;
    private Long invoiceTs;

    public AppointmentInvoiceDTO(AppointmentInvoice appointmentInvoice) {
        this.invoiceId = appointmentInvoice.getId();
        // this.invoiceNo = appointmentInvoice.getInvoiceNo();
        this.invoiceNo = CommonUtility.convertInvoiceNoIntoString(appointmentInvoice.getInvoiceNo());
        this.invoiceTs = appointmentInvoice.getInvoiceTs();
    }
}
