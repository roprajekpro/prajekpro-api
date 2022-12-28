package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ServiceSubCategoryDTO {
    private Long serviceSubCategoryId;
    private String serviceSubCategoryName;
    private String serviceSubCategoryDesc;
    private float reqPrice;
    private Long reqQty;

}
