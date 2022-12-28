package com.prajekpro.api.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SummaryForDashboard {

    private Long totalAppointments;
    private Long totalBookedAppointments;
    private Long totalConfirmedAppointments;
    private Long totalCompletedAppointments;
    private Long totalCancelledAppointments;

    public SummaryForDashboard(Long totalAppointments, Long totalBookedAppointments, Long totalCancelledAppointments, Long totalCompletedAppointments, Long totalConfirmedAppointments) {

        this.totalAppointments = totalAppointments;
        this.totalBookedAppointments = totalBookedAppointments;
        this.totalConfirmedAppointments = totalConfirmedAppointments;
        this.totalCompletedAppointments = totalCompletedAppointments;
        this.totalCancelledAppointments = totalCancelledAppointments;
    }
}
