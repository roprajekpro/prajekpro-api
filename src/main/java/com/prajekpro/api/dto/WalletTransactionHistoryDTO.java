package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProSubscription;
import com.prajekpro.api.domain.WalletTransactionHistory;
import com.prajekpro.api.enums.TransactionType;
import com.safalyatech.common.utility.CheckUtil;
import com.safalyatech.common.utility.CommonUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class WalletTransactionHistoryDTO {
    private Long id;
    private String date;
    private String customerName;
    private String invoiceNo;
    private String transactionId;
    private Double amount;
    private String transactionType;
    private Integer activeStatus;

    public WalletTransactionHistoryDTO(WalletTransactionHistory transHistory) {
        this.id = transHistory.getId();
        this.date = transHistory.getCreatedTs().toString();
        // this.invoiceNo = CheckUtil.hasValue(transHistory.getAppointmentDetails()) ? transHistory.getAppointmentDetails().getAppointmentInvoice().getInvoiceNo().toString() : "";
        // this.transactionId = transHistory.getTransactionId();
        this.transactionId = CommonUtility.convertTrxIdIntoString(transHistory.getTransactionId());
        this.amount = transHistory.getAmount();
        this.transactionType = transHistory.getTransactionType().name();
        this.customerName = CheckUtil.hasValue(transHistory.getAppointmentDetails()) ? transHistory.getAppointmentDetails().getBookedBy().getFullName() : "";
        this.activeStatus = transHistory.getActiveStatus();

        // invoice number
        if (transactionType.equalsIgnoreCase(TransactionType.LOAD_WALLET.name())) {
            ProSubscription proSubscription = transHistory.getProSubscription();
            if (CheckUtil.hasValue(proSubscription) && CheckUtil.hasValue(proSubscription.getSubscriptionInvoices())) {
                //TODO: Mapp this after proper mapping of Invoice No in PRO Subscription
                this.invoiceNo = CommonUtility.convertInvoiceNoIntoString(proSubscription.getSubscriptionInvoices().get(0).getInvoiceNo());
            }
        }

        if (transactionType.equalsIgnoreCase(TransactionType.APPOINTMENT.name())) {
            if (CheckUtil.hasValue(transHistory.getAppointmentDetails().getAppointmentInvoice())) {
                log.info("Appointment Invoice Details = {}", transHistory.getAppointmentDetails().getAppointmentInvoice().getInvoiceNo());
                //this.invoiceNo = transHistory.getAppointmentDetails().getAppointmentInvoice().getInvoiceNo().toString();
                this.invoiceNo = CommonUtility.convertInvoiceNoIntoString(transHistory.getAppointmentDetails().getAppointmentInvoice().getInvoiceNo());
            } else {
                log.error("Appointment With No Invoice Details = {}", transHistory.getAppointmentDetails().getId());
            }
        }
    }
}
