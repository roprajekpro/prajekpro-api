package com.prajekpro.api.dto;

import com.prajekpro.api.enums.SubscriptionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProSearchRequestBodyDTO {

    private List<Integer> activeStatus;
    private List<LatLongVO> latLongList;
    private List<SubscriptionStatus> subscriptionStatus;
    private String subscriptionDateFrom;
    private String subscriptionDateTill;
    private List<Long> ratings;
}
