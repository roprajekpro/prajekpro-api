package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum PaymentStatus {

    INITIATED(1),COMPLETED(2),FAILED(3);
    private Integer value;

    PaymentStatus(Integer value) {
        this.value = value;
    }

    public static Optional<PaymentStatus> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        as -> as.value == value)
                .findFirst();
    }

    public Integer value() {
        return this.value;
    }
}
