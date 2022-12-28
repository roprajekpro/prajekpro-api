package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentOtherServices;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentOtherServicesDTO {
    private Long id;
    private String serviceName;
    private Long reqQuantity;
    private Float unitPrice;

    public AppointmentOtherServicesDTO(AppointmentOtherServices otherServices) {
        this.id = otherServices.getId();
        this.serviceName=otherServices.getServiceName();
        this.reqQuantity = otherServices.getReqQuantity();
        this.unitPrice =otherServices.getUnitPrice();
    }
}
