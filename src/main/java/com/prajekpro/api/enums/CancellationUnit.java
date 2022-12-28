package com.prajekpro.api.enums;

public enum CancellationUnit {
    HOURS(24l), DAYS(25l);

    private long value;

    CancellationUnit(long value) {
        this.value = value;
    }

    public long value() {
        return this.value;
    }
}
