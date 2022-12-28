package com.prajekpro.api.enums;

public enum LookUpRowIds {
    OTHER(10l), AUTO_CANCEL(28l);
    private long value;

    LookUpRowIds(Long value) {
        this.value = value;
    }

    public Long value() {
        return this.value;
    }
}
