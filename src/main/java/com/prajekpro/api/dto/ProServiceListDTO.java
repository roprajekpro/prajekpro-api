package com.prajekpro.api.dto;

import com.prajekpro.api.domain.Services;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProServiceListDTO {
    private ProCancellationTimeDTO proCancellationTime;
    private Long serviceId;
    private String serviceName;
    private List<ServiceItemCategoriesDTO> serviceCategories;

    public ProServiceListDTO(Services services, List<ServiceItemCategoriesDTO> serviceCategories) {
        this.serviceId = services.getId();
        this.serviceName = services.getServiceName();
        this.serviceCategories = serviceCategories;
    }

    public ProServiceListDTO(Services services, List<ServiceItemCategoriesDTO> serviceCategories, ProCancellationTimeDTO proCancellationTimeDTO) {
        this.serviceId = services.getId();
        this.serviceName = services.getServiceName();
        this.serviceCategories = serviceCategories;
        this.proCancellationTime = proCancellationTimeDTO;
    }
}
