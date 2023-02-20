package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum Source {
    WEB(1), APP(2), CUSTOMER_APP(3), PRO_APP(4), GOOGLE_HANDLE(5),FACEBOOK_HANDLE(6);

    private Integer value;

    Source(Integer value) {
        this.value = value;
    }

    public static Optional<Source> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        s -> s.value == value)
                .findFirst();
    }

    public Integer value() {
        return this.value;
    }
}
