package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TransactionType {
    APPOINTMENT(1),LOAD_WALLET(2), SUBSCRIPTION(3), APPOINTMENT_CANCEL(4), RENEW_SUBSCRIPTION(5), CUST_APPOINTMENT_CANCEL(6), REFUND(7);

    private Integer value;

    TransactionType(Integer value) {
        this.value = value;
    }

    public static Optional<TransactionType> valueOf(Integer value) {

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
