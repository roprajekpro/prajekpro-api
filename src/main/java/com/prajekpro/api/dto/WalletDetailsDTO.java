package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProWalletDetails;
import com.prajekpro.api.domain.WalletTransactionHistory;
import com.safalyatech.common.utility.CheckUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class WalletDetailsDTO {

    private Long walletId;
    private Float amount;
    private Double effectiveBalance;
    private Double lockedAmount;
    private List<WalletTransactionHistoryDTO> walletTransactionHistoryList = new ArrayList<>();

    public WalletDetailsDTO(ProWalletDetails proWalletDetails, List<WalletTransactionHistory> walletTransactionHistory) {

        this.walletId = proWalletDetails.getId();
        if (CheckUtil.hasValue(proWalletDetails.getLockedAmount())) {
            this.lockedAmount = proWalletDetails.getLockedAmount().doubleValue();
        } else {
            this.lockedAmount = 0.0d;
        }
        this.effectiveBalance = proWalletDetails.getAmount().doubleValue()-this.lockedAmount;

        log.debug("transactionHistory list is empty or not = {}", walletTransactionHistory.isEmpty());
        for (WalletTransactionHistory transHistory : walletTransactionHistory) {
            this.walletTransactionHistoryList.add(new WalletTransactionHistoryDTO(transHistory));
        }
    }
}
