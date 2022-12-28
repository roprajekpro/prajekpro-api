package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentRequestedServiceCategories;
import com.prajekpro.api.domain.AppointmentRequestedServices;
import com.safalyatech.common.enums.ActiveStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentServicesDTO {
    private Long appointmentRequestedServiceId;
    private Long serviceId;
    private String serviceName;
    private String date;
    private CommonFieldsDTO timeSlot;
    private List<AppointmentServiceCategoryDTO> appointmentServiceCategory;



    public AppointmentServicesDTO(AppointmentRequestedServices services) {
        this.appointmentRequestedServiceId = services.getId();
        this.serviceId = services.getServices().getId();
        this.serviceName = services.getServices().getServiceName();
        this.date = services.getAppointmentDate();
        this.timeSlot = new CommonFieldsDTO(services.getTimeSlotId(),services.getAppointmentTime());
        List<AppointmentRequestedServiceCategories> appointmentRequestedServiceCategories = services.getAppointmentRequestedServiceCategories();

        this.appointmentServiceCategory = new ArrayList<>();
        for(AppointmentRequestedServiceCategories serviceCategory : appointmentRequestedServiceCategories){
            if(serviceCategory.getActiveStatus() == ActiveStatus.ACTIVE.value()){
                appointmentServiceCategory.add(new AppointmentServiceCategoryDTO(serviceCategory));

            }
        }
    }
}
