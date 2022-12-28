package com.prajekpro.api.enums;

public enum CouponCodeType {
    SUBSCRIPTION(1l);

    private long value;

    CouponCodeType(Long value) {
        this.value = value;
    }

    public Long value() {
        return this.value;
    }
}
