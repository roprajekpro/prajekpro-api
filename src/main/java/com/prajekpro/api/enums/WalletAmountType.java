package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum WalletAmountType {

    ESSENTIAL(1), APPOINTMENT_COMISSION(2), SUBSCRIPTION_COMISSION(3), CANCELLATION_PENALTY(4);

    private Integer value;

    WalletAmountType(Integer value) {
        this.value = value;
    }

    public static Optional<WalletAmountType> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        tt -> tt.value == value)
                .findFirst();
    }

    public Integer value() {
        return this.value;
    }
}
