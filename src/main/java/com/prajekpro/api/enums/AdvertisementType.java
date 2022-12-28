package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum AdvertisementType {

    GENERAL_ADVERTISEMENT(1);

    private Integer value;

    AdvertisementType(Integer value) {
        this.value = value;
    }

    public static Optional<AdvertisementType> valueOf(Integer value) {

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
