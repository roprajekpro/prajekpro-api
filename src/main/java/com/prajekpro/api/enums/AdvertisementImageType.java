package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum AdvertisementImageType {

    CLICK_IMAGE(1),POP_UP_IMAGE(2);

    private Integer value;

    AdvertisementImageType(Integer value) {
        this.value = value;
    }

    public static Optional<AdvertisementImageType> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        as -> as.value == value)
                .findFirst();
    }

    public Integer value() {
        return this.value;
    }
}
