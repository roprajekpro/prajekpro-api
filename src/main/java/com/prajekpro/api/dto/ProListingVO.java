package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class ProListingVO {

    private Long id;
    private String name;
    private MetaData<ProDetailsDTO> proMetaData;

    public ProListingVO(
            Long id,
            String serviceName,
            MetaData<ProDetailsDTO> proDetailsMetaData) {
        this.id = id;
        this.name = serviceName;
        this.proMetaData = proDetailsMetaData;
    }

}
