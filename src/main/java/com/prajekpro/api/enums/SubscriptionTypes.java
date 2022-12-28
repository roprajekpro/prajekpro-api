package com.prajekpro.api.enums;

public enum SubscriptionTypes {

    NEW_SUBSCRIPTION(1), RENEW_SUBSCRIPTION(2), FREE_SUBSCRIPTION(3);

    private Integer value;

    SubscriptionTypes(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
