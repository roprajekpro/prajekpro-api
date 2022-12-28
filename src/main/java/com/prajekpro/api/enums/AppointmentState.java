package com.prajekpro.api.enums;

import java.util.*;

public enum AppointmentState {
    ALL(0, "All"),
    BOOKED(1, "Booked"),
    CONFIRMED(2, "Confirmed"),
    CHECKED_IN(3, "Checked In"),
    CHECKED_OUT(4, "Checked Out"),
    COMPLETED(5, "Completed"),
    CANCELLED(6, "Cancelled");

    private Integer value;
    private String text;

    AppointmentState(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public static Optional<AppointmentState> valueOf(Integer value) {

        return Arrays
                .stream(values())
                .filter(
                        as -> as.value == value)
                .findFirst();
    }

    public Integer value() {
        return this.value;
    }


    public static Optional<AppointmentState> fromText(String text) {

        return Arrays
                .stream(values())
                .filter(as -> as.text.equalsIgnoreCase(text))
                .findFirst();
    }

    public String text() {
        return this.text;
    }


}
