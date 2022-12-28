package com.prajekpro.api.dto;

import com.safalyatech.common.dto.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProDetailsAndOverviewsDTO {
    private PPRegisterVO proBasicDetails;
    private List<ProvidedServiceDetailsDTO> providedServiceDetails;
    private ProjectOverviewDTO projectOverview;

    public ProDetailsAndOverviewsDTO(PPRegisterVO registerVO, ProjectOverviewDTO projectOverviewDTO) {
        this.proBasicDetails = registerVO;
        this.projectOverview = projectOverviewDTO;
    }

    public ProDetailsAndOverviewsDTO(PPRegisterVO registerVO,
                                     List<ProvidedServiceDetailsDTO> providedServiceDetails, ProjectOverviewDTO projectOverviewDTO) {
        this.proBasicDetails = registerVO;
        this.providedServiceDetails = providedServiceDetails;
        this.projectOverview = projectOverviewDTO;
    }
}
