package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum SubscriptionStatus {
    ACTIVE(0), DEACTIVE(1), INITIATED(2), PENDING(3), APPROVAL(4), REJECTED(5);

    private Integer value;

    SubscriptionStatus(Integer value) {
        this.value = value;
    }

    public static Optional<SubscriptionStatus> valueOf(Integer value) {

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
