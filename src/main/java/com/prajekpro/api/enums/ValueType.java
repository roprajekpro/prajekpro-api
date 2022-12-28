package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ValueType {
    PERCENTAGE(1),ABSOLUTE(2);
    private Integer value;

    ValueType(Integer value) {
        this.value = value;
    }

    public static Optional<ValueType> valueOf(Integer value) {

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
