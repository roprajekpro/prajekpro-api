package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum MessageType {

    TEXT(1),DOCUMENT(2);

    private Integer value;

    MessageType(Integer value) {
        this.value = value;
    }

    public static Optional<MessageType> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        mt -> mt.value == value)
                .findFirst();
    }

    public Integer value() {
        return this.value;
    }
}
