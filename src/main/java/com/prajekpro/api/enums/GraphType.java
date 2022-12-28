package com.prajekpro.api.enums;

import java.util.*;

public enum GraphType {
    BAR(1), STACKED_BAR(2), PIE(3), DONUT(4);

    private Integer value;

    GraphType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }

    public static Optional<GraphType> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        nt -> nt.value == value)
                .findFirst();
    }
}
