package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentDetails;
import com.prajekpro.api.enums.AppointmentState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CustomerAppointmentDTO {
    private String proName;
    private String serviceName;
    private String appointmentDate;
    private AppointmentState appointmentStatus;

    public CustomerAppointmentDTO(AppointmentDetails details) {
        this.proName = details.getBookedFor().getUserDetails().getFullName();
        this.serviceName = details.getAppointmentRequestedServices().get(0).getServices().getServiceName();
        this.appointmentDate = details.getAppointmentRequestedServices().get(0).getAppointmentDate();
        this.appointmentStatus = details.getState();
    }
}
