package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentRequestedServices;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AppointmentRequestedServicesDTO {
    private Long appointmentRequestedServiceId;
    private Long serviceId;
    private String serviceName;
    private String appointmentTime;
    private Long appointmentTimeSlotId;
    private String date;

    public AppointmentRequestedServicesDTO(AppointmentRequestedServices services) {

        this.appointmentRequestedServiceId = services.getId();
        this.serviceId = services.getServices().getId();
        this.serviceName = services.getServices().getServiceName();
        this.date = services.getAppointmentDate();
        this.appointmentTimeSlotId = services.getTimeSlotId();
        this.appointmentTime = services.getAppointmentTime();
    }
}
