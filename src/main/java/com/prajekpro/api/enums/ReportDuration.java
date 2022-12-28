package com.prajekpro.api.enums;

import java.util.*;

public enum ReportDuration {
    ITD(1), YTD(2), QTD(3), MTD(4), WEEKLY(5);

    private Integer value;

    ReportDuration(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }

    public static Optional<ReportDuration> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        nt -> nt.value == value)
                .findFirst();
    }
}
