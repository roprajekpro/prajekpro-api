package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentRequestedServiceSubCategories;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentServiceSubCategoryDTO {
    private Long appointmentRequestedServiceSubCategoryId;
    private Long serviceSubCategoryId;
    private String serviceSubCategoryName;
    private String serviceSubCategoryDesc;
    private float reqPrice;
    private Long reqQty;

    public AppointmentServiceSubCategoryDTO(AppointmentRequestedServiceSubCategories serviceSubCategory) {
        this.appointmentRequestedServiceSubCategoryId = serviceSubCategory.getId();
        this.serviceSubCategoryId = serviceSubCategory.getServiceItemSubCategory().getId();
        this.serviceSubCategoryName = serviceSubCategory.getServiceItemSubCategory().getItemSubCategoryName();
        this.serviceSubCategoryDesc = serviceSubCategory.getServiceItemSubCategory().getItemSubCategoryDesc();
        this.reqPrice = serviceSubCategory.getRequestedPrice();
        this.reqQty = serviceSubCategory.getRequestedQty();
    }
}
