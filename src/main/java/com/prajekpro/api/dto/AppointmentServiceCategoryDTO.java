package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentRequestedServiceCategories;
import com.prajekpro.api.domain.AppointmentRequestedServiceSubCategories;
import com.safalyatech.common.enums.ActiveStatus;
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
public class AppointmentServiceCategoryDTO {
    private Long appointmentRequestedServiceCategoryId;
    private Long serviceCategoryId;
    private String serviceCategoryName;
    private List<AppointmentServiceSubCategoryDTO> appointmentServiceSubCategory;


    public AppointmentServiceCategoryDTO(AppointmentRequestedServiceCategories serviceCategory) {
        this.appointmentRequestedServiceCategoryId = serviceCategory.getId();
        this.serviceCategoryId = serviceCategory.getServiceItemCategory().getId();
        this.serviceCategoryName = serviceCategory.getServiceItemCategory().getValue();
        List<AppointmentRequestedServiceSubCategories> appointmentRequestedServiceSubCategories = serviceCategory.getAppointmentRequestedServiceSubCategories();
        log.debug("count of subcategorylist ={}", appointmentRequestedServiceSubCategories.size());

        this.appointmentServiceSubCategory = new ArrayList<>();
        for (AppointmentRequestedServiceSubCategories serviceSubCategory : appointmentRequestedServiceSubCategories) {
            if (serviceSubCategory.getActiveStatus() == ActiveStatus.ACTIVE.value()) {
                log.debug("service subcategory added");
                appointmentServiceSubCategory.add(new AppointmentServiceSubCategoryDTO(serviceSubCategory));
            }
        }
    }


}
