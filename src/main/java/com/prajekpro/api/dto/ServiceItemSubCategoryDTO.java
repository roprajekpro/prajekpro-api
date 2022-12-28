package com.prajekpro.api.dto;

import com.prajekpro.api.domain.Currency;
import com.prajekpro.api.domain.ServiceItemSubCategory;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ServiceItemSubCategoryDTO {
    private Long id;
    private float defaultFillipinoPrice;
    private String subCategoryName;
    private String subCategoryDesc;
    private Long parentId = 0l;
    private int isActive;
    private float proSubCategoryPrice;
    private Long proServicePricingId;
    private Currency currency;
    private String currencyId;


    public ServiceItemSubCategoryDTO(ServiceItemSubCategory serviceItemSubCategory) {

        this.id = serviceItemSubCategory.getId();
        this.defaultFillipinoPrice = serviceItemSubCategory.getDefaultFillipinoPrice();
        this.subCategoryName = serviceItemSubCategory.getItemSubCategoryName();
        this.subCategoryDesc = serviceItemSubCategory.getItemSubCategoryDesc();
        this.parentId = serviceItemSubCategory.getParentId();
        this.isActive = serviceItemSubCategory.getActiveStatus();
        this.currency = serviceItemSubCategory.getCurrency();
    }
}
