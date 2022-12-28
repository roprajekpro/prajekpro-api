package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProServiceItemsPricing;
import com.prajekpro.api.domain.ServiceItemSubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ItemSubCategoryPricingDetailsDTO {

    private Long id;
    private String itemSubCategoryName;
    private String itemSubCategoryDescription;
    private Float itemSubCategoryPrice;
    private String currency;
    private Long serviceId;

    public ItemSubCategoryPricingDetailsDTO(ProServiceItemsPricing input) {

        updateSubCategoryProperties(input.getServiceItemSubcategory());

        this.itemSubCategoryPrice = input.getPrice();
        this.currency = input.getCurrency().getCode();
    }

    public ItemSubCategoryPricingDetailsDTO(ServiceItemSubCategory serviceItemSubCategory) {
        updateSubCategoryProperties(serviceItemSubCategory);
    }

    private void updateSubCategoryProperties(ServiceItemSubCategory subCategory) {
        this.id = subCategory.getId();
        this.itemSubCategoryName = subCategory.getItemSubCategoryName();
        this.itemSubCategoryDescription = subCategory.getItemSubCategoryDesc();
        this.serviceId = subCategory.getServiceItemCategory().getServices().getId();
    }


}
