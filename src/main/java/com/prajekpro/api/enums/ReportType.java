package com.prajekpro.api.enums;

import java.util.*;

public enum ReportType {
    SALES(1), CA(2);

    private Integer value;

    ReportType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }

    public static Optional<ReportType> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        nt -> nt.value == value)
                .findFirst();
    }
}
