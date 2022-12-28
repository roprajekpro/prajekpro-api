package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AdvertisementImage;
import com.prajekpro.api.enums.AdvertisementImageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AdvertisementImageDTO {

    private Long id;
    private String advtImgDisplayNm;
    private String advtImgSavedNm;
    private String advtImgExtn;
    private String advtImgSaveDirNm;
    private AdvertisementImageType imageType;

    public AdvertisementImageDTO(AdvertisementImage image) {
        this.id = image.getId();
        this.imageType = image.getImageType();
        this.advtImgDisplayNm = image.getAdvtImgDisplayNm();
        this.advtImgSavedNm = image.getAdvtImgSavedNm();
        this.advtImgSaveDirNm = image.getAdvtImgSaveDirNm();
        this.advtImgExtn = image.getAdvtImgExtn();
    }
}
