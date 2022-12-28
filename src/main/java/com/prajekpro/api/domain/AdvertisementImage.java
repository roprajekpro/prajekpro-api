package com.prajekpro.api.domain;

import com.prajekpro.api.enums.AdvertisementImageType;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.dto.FileDetailsDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "advertisement_image")
public class AdvertisementImage extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ADVERTISEMENT_ID")
    private Advertisements advertisements;

    @Column(name = "ADVT_IMG_DISPLAY_NM")
    private String advtImgDisplayNm;

    @Column(name = "ADVT_IMG_SAVED_NM")
    private String advtImgSavedNm;

    @Column(name = "ADVT_IMG_EXTN")
    private String advtImgExtn;

    @Column(name = "ADVT_IMG_SAVE_DIR_NM")
    private String advtImgSaveDirNm;

    @Column(name = "IMAGE_TYPE")
    private AdvertisementImageType imageType;

    public AdvertisementImage(FileDetailsDTO fileDetailsDTO, Advertisements advertisements) {
        this.advertisements = advertisements;
        this.advtImgDisplayNm = fileDetailsDTO.getFileName();
        this.advtImgExtn = fileDetailsDTO.getFileExtension();
        this.advtImgSavedNm = fileDetailsDTO.getFileSavedName();
        this.advtImgSaveDirNm = fileDetailsDTO.getFilePath();
    }
}
