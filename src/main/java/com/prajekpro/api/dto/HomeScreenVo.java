package com.prajekpro.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prajekpro.api.domain.*;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.utility.CheckUtil;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HomeScreenVo {

    private CurrentLocationDetailsDTO currentLocationDetails;
    private UserDetailsVO userDetails;
    private MetaData<Detail> notificationMetaData;
    private MetaData<AdvertisementDTO> advertisementMetaData;
    private MetaData<Detail> servicesMetaData;
    private MetaData<UpcomingAppointmentVO> upcomingAppointmentsMetaData;
    private MetaData<OfferDetailsVO> offersMetaData;
    private CommercialAdvertisementDTO commercialAdvertisement;

    @JsonIgnore
    public void updateProperties(
            CurrentLocationDetailsDTO currentLocationDetails,
            Users users,
            List<NotificationDetails> notificationsData,
            List<AdvertisementDTO> advertisementDetails,
            List<Services> services,
            List<Appointments> appointments,
            List<OfferDetails> offerDetails,
            CommercialAdvertisement commercialAdvertisement) {

        this.currentLocationDetails = currentLocationDetails;
        this.userDetails = users == null ? null : new UserDetailsVO(users);
        this.notificationMetaData = new MetaData<Detail>(
                notificationsData
                        .stream()
                        .map(
                                nd -> nd.getDetail())
                        .collect(
                                Collectors.toList()));
        this.advertisementMetaData = new MetaData<AdvertisementDTO>(advertisementDetails);
        this.servicesMetaData = new MetaData<Detail>(
                services
                        .stream()
                        .map(
                                sr -> sr.getDetail())
                        .collect(
                                Collectors.toList()));
        this.upcomingAppointmentsMetaData = new MetaData<UpcomingAppointmentVO>(
                appointments
                        .stream()
                        .map(
                                ap -> ap.getUpcomingAppointmentVO())
                        .collect(
                                Collectors.toList()));
        this.offersMetaData = new MetaData<OfferDetailsVO>(
                offerDetails
                        .stream()
                        .map(
                                of -> of.getOfferDetailsVO())
                        .collect(
                                Collectors.toList()));

        if (CheckUtil.hasValue(commercialAdvertisement))
            this.commercialAdvertisement = new CommercialAdvertisementDTO(commercialAdvertisement);
    }

}
