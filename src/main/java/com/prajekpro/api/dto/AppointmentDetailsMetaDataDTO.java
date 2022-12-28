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
public class AppointmentDetailsMetaDataDTO {
    private Long serviceId;
    private List<AppointmentBookingDTO> appointmentDetails;

    public AppointmentDetailsMetaDataDTO(Long id, List<AppointmentBookingDTO> appointmentDetail) {
        this.serviceId = id;
        this.appointmentDetails = appointmentDetail;
    }
}
