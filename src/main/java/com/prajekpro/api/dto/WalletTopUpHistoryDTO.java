package com.prajekpro.api.dto;


import com.prajekpro.api.domain.WalletTopUpHistory;
import com.safalyatech.common.utility.CommonUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class WalletTopUpHistoryDTO {
    private Long id;
    private Double amount;
    private String date;
    private String transactionId;
    private String transactionType;
    private Long sourceId;
    private String source;
    private String paymentMethod;
    private Integer activeStatus;


    public WalletTopUpHistoryDTO(WalletTopUpHistory topUpHistory) {
        this.id = topUpHistory.getId();
        this.amount = topUpHistory.getAmount();
        this.date = topUpHistory.getCreatedTs().toString();
        //this.transactionId = topUpHistory.getWalletTransactionHistory().getTransactionId();
        this.transactionId = CommonUtility.convertTrxIdIntoString(topUpHistory.getWalletTransactionHistory().getTransactionId());
        this.transactionType = topUpHistory.getTransactionType().name();
        this.sourceId = topUpHistory.getPpLookUp().getId();
        this.source = topUpHistory.getPpLookUp().getValue();
        this.paymentMethod = topUpHistory.getWalletTransactionHistory().getPaymentDetails().getPaymentMethod();
        this.activeStatus = topUpHistory.getActiveStatus();
    }
}
