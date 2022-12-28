package com.prajekpro.api.dto;


import com.prajekpro.api.domain.ServiceItemCategory;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServiceItemCategoriesDTO {

    private long id;
    private String storedValue;
    private String displayValue;
    private int isActive;
    public List<ServiceItemSubCategoryDTO> serviceItemSubCategoryList;

    public ServiceItemCategoriesDTO(ServiceItemCategory serviceItemCategory) {
        this.id = serviceItemCategory.getId();
        this.storedValue = serviceItemCategory.getReference();
        this.displayValue = serviceItemCategory.getValue();
        this.isActive = serviceItemCategory.getActiveStatus();
    }
}
