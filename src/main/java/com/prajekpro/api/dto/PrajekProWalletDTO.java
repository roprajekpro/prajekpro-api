package com.prajekpro.api.dto;


import com.prajekpro.api.domain.Currency;
import com.safalyatech.common.utility.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.math3.util.Precision;


@Setter
@Getter
@ToString
@NoArgsConstructor
public class PrajekProWalletDTO {
    private Double totalPrajekProWalletAmount;
    private Double totalPrajekProWalletCommissionAmount;
    private Double totalPrajekProWalletEssentialAmount;
    private Currency currencies;

    public PrajekProWalletDTO(Double totalPrajekProWalletAmount, Double totalWalletCommitionAmount, Double totalWalletEssentialAmount) {
        this.totalPrajekProWalletAmount = Precision.round(checkNullOrGetDefaultValue(totalPrajekProWalletAmount),2);
        this.totalPrajekProWalletCommissionAmount = Precision.round(checkNullOrGetDefaultValue(totalWalletCommitionAmount),2);
        this.totalPrajekProWalletEssentialAmount = Precision.round(checkNullOrGetDefaultValue(totalWalletEssentialAmount),2);

    }

    private double checkNullOrGetDefaultValue(Double value) {
        return CheckUtil.hasValue(value) ? value : 0d;
    }
}
