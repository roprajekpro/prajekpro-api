package com.prajekpro.api.enums;

public enum AppointmentNotificationEnum {
    APPOINTMENT_BOOKED("Appointment booked"),
    APPOINTMENT_UPDATED("Appointment updated"),
    INVOICE_GENERATED("Invoice generated");

    private String value;

    AppointmentNotificationEnum(String value) {
        this.value = value;
    }


    public String value() {
        return this.value;
    }
}
