package com.prajekpro.api.dto;

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
public class ServiceCategoryDTO {
    private Long serviceCategoryId;
    private String serviceCategoryName;
    private List<ServiceSubCategoryDTO> serviceSubCategoryList;

    public ServiceCategoryDTO(ServiceItemCategory category) {
    }
}
