package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentDetails;
import com.prajekpro.api.domain.AppointmentRequestedServices;
import com.prajekpro.api.domain.ProDetails;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentDTO {

    private String proName;
    private String customerName;
    private String appointmentState;
    private Long appointmentId;
    private Long proId;
    private String userId;
    private String customerId;
    private PaymentDTO payment;
    private List<AppointmentRequestedServicesDTO> appointmentServices = new ArrayList<>();

    public AppointmentDTO(AppointmentDetails details) {
        this.appointmentId = details.getId();
        ProDetails bookedFor = details.getBookedFor();
        this.proId = bookedFor.getId();
        Users proUserDetails = bookedFor.getUserDetails();
        this.proName = proUserDetails.getFullName();
        this.userId = proUserDetails.getUserId();
        this.appointmentState = details.getState().name();
        this.customerName = details.getBookedBy().getFullName();
        this.customerId = details.getBookedBy().getUserId();
        List<AppointmentRequestedServices> appointmentRequestedServices = details.getAppointmentRequestedServices();
        for (AppointmentRequestedServices services : appointmentRequestedServices) {
            appointmentServices.add(new AppointmentRequestedServicesDTO(services));
        }
    }
}
