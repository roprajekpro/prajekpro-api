package com.prajekpro.api.domain;

import com.prajekpro.api.dto.AdvertisementDTO;
import com.prajekpro.api.enums.AdvertisementType;
import com.safalyatech.common.domains.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "advertisements")
public class Advertisements extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "TYPE")
    private AdvertisementType type;

    @OneToMany(targetEntity = AdvertisementImage.class, mappedBy = "advertisements",cascade = CascadeType.ALL)
    private List<AdvertisementImage> advertisementImageList;

    public Advertisements(String title, AdvertisementType type, Integer sortOrder) {
        this.sortOrder = sortOrder;
        this.title = title;
        this.type = type;
    }
}
