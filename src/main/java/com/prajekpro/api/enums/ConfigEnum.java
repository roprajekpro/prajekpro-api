package com.prajekpro.api.enums;

import java.util.Arrays;
import java.util.Optional;

public enum ConfigEnum {

    APPOINTMENT_CANCELLATION_PERCENT(1), APPOINTMENT_PRO_CNCL_PNLTY_PERCENTAGE(2),APPOINTMENT_COMPLETION_PRAJEKPRO_PERCENT(3),
    WALLET_DEFAULT_LIMIT(4), VAT_PERC(5), SERVICE_LOCATION_RADIUS(6);

    private Integer value;

    ConfigEnum(Integer value) {
        this.value = value;
    }

    public static Optional<ConfigEnum> valueOf(Integer value) {

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
