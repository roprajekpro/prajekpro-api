package com.prajekpro.api.dto;

import com.prajekpro.api.enums.WalletAmountType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PrajekProWalletSearchRequestDTO {

    private List<WalletAmountType> walletAmountTypes;
}
