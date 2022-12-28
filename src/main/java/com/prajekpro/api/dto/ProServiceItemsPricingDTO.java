package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ServiceItemCategory;
import com.prajekpro.api.domain.ServiceItemSubCategory;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProServiceItemsPricingDTO {

    private Long id;
    private String itemCategoryName;
    private List<ItemSubCategoryPricingDetailsDTO> itemSubCategoryDetails = new ArrayList<>();

    public ProServiceItemsPricingDTO(ServiceItemCategory category) {
        this.id = category.getId();
        this.itemCategoryName = category.getValue();
    }

    public ProServiceItemsPricingDTO(ServiceItemSubCategory serviceItemSubCategory) {
    }
}
