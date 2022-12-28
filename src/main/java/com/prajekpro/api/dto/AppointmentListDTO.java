package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentListDTO {
    private List<AppointmentDTO> currentAppointments;
    private List<AppointmentDTO> pastAppointments;

    public AppointmentListDTO(List<AppointmentDTO> currentAppointmentList, List<AppointmentDTO> pastAppointmentList) {
        this.currentAppointments = currentAppointmentList;
        this.pastAppointments = pastAppointmentList;
    }
}
