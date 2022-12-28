package com.prajekpro.api.dto;

import com.prajekpro.api.domain.Currency;
import com.prajekpro.api.domain.PrajekProWalletDetails;
import com.prajekpro.api.domain.ProDetails;
import com.prajekpro.api.enums.WalletAmountType;
import com.safalyatech.common.utility.CheckUtil;
import com.safalyatech.common.utility.CommonUtility;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
@NoArgsConstructor
public class PrajekProWalletResponseWrapper {

    private Long id;

    private Long proId;

    private String proName;

    private Double amount;

    private WalletAmountType walletAmountType;

    private Currency currency;

    private String transactionId;

    private Long transactionDate;

    public PrajekProWalletResponseWrapper(PrajekProWalletDetails walletDetails) {
        this.id = walletDetails.getId();
        this.proId = walletDetails.getProDetails().getId();
        this.proName = walletDetails.getProDetails().getUserDetails().getFullName();
        this.currency = walletDetails.getCurrency();
        if (CheckUtil.hasValue(walletDetails.getWalletTransactionHistory())) {
            this.transactionId = CommonUtility.convertTrxIdIntoString(walletDetails.getWalletTransactionHistory().getTransactionId());
            this.transactionDate = walletDetails.getWalletTransactionHistory().getCreatedTs();
        }
        this.amount = walletDetails.getAmount();
        this.walletAmountType = walletDetails.getWalletAmountType();
    }
}
