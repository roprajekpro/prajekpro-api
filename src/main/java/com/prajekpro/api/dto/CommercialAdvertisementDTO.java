package com.prajekpro.api.dto;

import com.prajekpro.api.domain.CommercialAdvertisement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class CommercialAdvertisementDTO implements Serializable {
    private Long id;
    private String url;

    public CommercialAdvertisementDTO(CommercialAdvertisement commercialAdvertisement) {
        if (null != commercialAdvertisement) {
            this.id = commercialAdvertisement.getId();
            this.url = commercialAdvertisement.getUrl();
        }
    }
}
