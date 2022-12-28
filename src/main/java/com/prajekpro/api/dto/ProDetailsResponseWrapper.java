package com.prajekpro.api.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProDetailsResponseWrapper {

    private Long id;
    private String name;
    private String serviceDescription;
    private ProDetailsDTO proDetails;
    private MetaData<ProServiceItemsPricingDTO> serviceItemCategoryMetaData;
    private MetaData<ProReviewsDTO> reviewsMetaData;
    private ProCancellationTimeDTO proCancellationTimeMetaData;
}
