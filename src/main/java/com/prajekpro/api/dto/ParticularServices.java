package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProServiceItemsPricing;
import com.prajekpro.api.domain.ServiceItemCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ParticularServices {

    private Long serviceId;
    private String serviceName;
    private List<ServiceCategoryDTO> serviceCategoryList;

    public ParticularServices(ProServiceItemsPricing servicesPricing) {
        this.serviceId = servicesPricing.getServices().getId();
        this.serviceName = servicesPricing.getServices().getServiceName();
        List<ServiceItemCategory> serviceItemCategory = servicesPricing.getServices().getServiceItemCategories();
        for (ServiceItemCategory category : serviceItemCategory) {
            this.serviceCategoryList.add(new ServiceCategoryDTO(category));
        }
    }
}
