package com.prajekpro.api.dto;

import com.safalyatech.common.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProAppointmentListDTO {
    private List<ProvidedServiceDetailsDTO> providedServiceDetails;
    private List<AppointmentBookingDTO> currentAppointments;
    private List<AppointmentBookingDTO> pastAppointments;

    public ProAppointmentListDTO(List<AppointmentBookingDTO> appointmentInConfirmedState, List<AppointmentBookingDTO> appointmentInPastState, List<ProvidedServiceDetailsDTO> providedServiceDetails) {

        this.currentAppointments = appointmentInConfirmedState;
        this.pastAppointments = appointmentInPastState;
        this.providedServiceDetails = providedServiceDetails;
    }
}
