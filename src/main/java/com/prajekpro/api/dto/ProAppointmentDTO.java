package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentDetails;
import com.prajekpro.api.enums.AppointmentState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProAppointmentDTO {
    public String customerName;
    public String serviceName;
    public String appointmentDate;
    public AppointmentState appointmentStatus;

    public ProAppointmentDTO(AppointmentDetails details) {
        this.customerName = details.getBookedBy().getFullName();
        this.serviceName = details.getAppointmentRequestedServices().get(0).getServices().getServiceName();
        this.appointmentDate = details.getAppointmentRequestedServices().get(0).getAppointmentDate();
        this.appointmentStatus = details.getState();
    }
}
