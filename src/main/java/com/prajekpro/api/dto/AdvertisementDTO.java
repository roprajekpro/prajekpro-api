package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AdvertisementImage;
import com.prajekpro.api.domain.Advertisements;
import com.prajekpro.api.enums.AdvertisementType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AdvertisementDTO {

    private Long id;
    private Integer sortOrder;
    private String title;
    private AdvertisementType type;
    private int isActive;
    private List<AdvertisementImageDTO> advertisementImages = new ArrayList<>();

    public AdvertisementDTO(Advertisements advertisement) {
        log.debug("Advertisement Id ={}", advertisement.getId());
        this.id = advertisement.getId();
        this.sortOrder = advertisement.getSortOrder();
        this.title = advertisement.getTitle();
        this.type = advertisement.getType();

        for (AdvertisementImage image : advertisement.getAdvertisementImageList()) {
            this.advertisementImages.add(new AdvertisementImageDTO(image));
            log.debug("image added in list");
        }
        this.isActive = advertisement.getActiveStatus();
    }
}
