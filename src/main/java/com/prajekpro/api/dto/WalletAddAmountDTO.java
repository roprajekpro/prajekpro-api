package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProWalletDetails;
import com.safalyatech.common.utility.CheckUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
public class WalletAddAmountDTO {
    private Long walletId;
    private Double amount;
    private Double effectiveBalance;
    private Double lockedAmount;
    private Long proId;
    private Long sourceId;
    private List<WalletTopUpHistoryDTO> walletTopUpHistoryList;

    public WalletAddAmountDTO(ProWalletDetails proWalletDetails) {
        this.walletId = proWalletDetails.getId();
        if (CheckUtil.hasValue(proWalletDetails.getLockedAmount())) {
            this.lockedAmount = proWalletDetails.getLockedAmount().doubleValue();
        } else {
            this.lockedAmount = 0.0d;
        }
        log.debug("Wallet amount = {}", proWalletDetails.getAmount());
        this.effectiveBalance = proWalletDetails.getAmount().doubleValue();
    }
}
