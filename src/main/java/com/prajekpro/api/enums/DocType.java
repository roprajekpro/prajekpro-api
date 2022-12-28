package com.prajekpro.api.enums;

public enum DocType {
    CERT_N_LICENSES(1),
    PRAJEPRO_RESULTS(2),
    APPT_CHECK_IN(3),
    APPT_CHECK_OUT(4);

    public static final String DEFAULT_VALUE = "CERT_N_LICENSES";
    private int value;

    DocType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
