package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProSubscription;
import com.safalyatech.common.utility.CheckUtil;
import com.safalyatech.common.utility.CommonUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SubscriptionListDTO {
    private Long subscriptionId;
    private Long proId;
    private Double amount;
    private String dateOfSubscription;
    private String transactionId;
    private String invoice;
    private String paymentMethod;
    private Integer activeStatus;

    public SubscriptionListDTO(ProSubscription subscription) {
        this.subscriptionId = subscription.getId();
        this.proId = subscription.getProDetails().getId();
        this.amount = subscription.getMasterSubscription().getSubscriptionAmount();
        this.dateOfSubscription = subscription.getDateOfSubscription();
        this.activeStatus = subscription.getActiveStatus();
        if (CheckUtil.hasValue(subscription.getWalletTransactionHistory())) {
            // this.transactionId = subscription.getWalletTransactionHistory().getTransactionId();
            this.transactionId = CommonUtility.convertTrxIdIntoString(subscription.getWalletTransactionHistory().getTransactionId());
        }
        //TODO: Fill this details
        if (CheckUtil.hasValue(subscription.getSubscriptionInvoices())) {
            this.invoice = CommonUtility.convertInvoiceNoIntoString(subscription.getSubscriptionInvoices().get(0).getInvoiceNo());
        }
        this.paymentMethod = subscription.getPaymentDetails().getPaymentMethod();
    }
}
