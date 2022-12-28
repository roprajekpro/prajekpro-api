package com.prajekpro.api.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProDetailsDTO {

    private Long id;
    private String userId;
    private String name;
    private String profileImage;
    private Long startingCost;
    private String currency;
    private Long startRating;
    private Long ratedBy;
    private float experienceInYears;
    private String totalJobsCompleted;
    private String availabilityStatus;
    private String aboutText;
    @JsonProperty(value = "isCertified")
    private boolean isCertified;
    @JsonProperty(value = "isPrajekproVerified")
    private boolean isPrajekproVerified;
}
