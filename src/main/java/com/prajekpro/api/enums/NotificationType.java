package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum NotificationType {

    GENERAL(1),
    CHAT(2),
    APPOINTMENT(3),
    USER_STATUS(4),
    PAYMENT(5),
    WALLET(6),
    SUBSCRIPTION(7);

    private Integer value;

    NotificationType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }

    public static Optional<NotificationType> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        nt -> nt.value == value)
                .findFirst();
    }
}

