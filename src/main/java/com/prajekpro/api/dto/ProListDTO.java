package com.prajekpro.api.dto;

import com.fasterxml.jackson.annotation.*;
import com.prajekpro.api.domain.ProDocuments;
import com.prajekpro.api.enums.SubscriptionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProListDTO {
    private Long proId;
    private String fullName;
    private String statusRemark;
    private SubscriptionStatus subscriptionStatus;
    private String dateOfSubscription;
    private String subscriptionExpiryDate;
    private Long ratings;
    private LocationDetailsDTO proServicesLocations;
    private Integer totalAppointments;
    private Integer todaysAppointments;
    private Integer activeStatus;
    private String prflImgSaveDirNm;
    private String prflImgSavedNm;
    private Set<ProDocuments> documents;
}
