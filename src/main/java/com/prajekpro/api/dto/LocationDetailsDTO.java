package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class LocationDetailsDTO {

    private Float latitude;
    private Float longitude;
    private String location;

    public LocationDetailsDTO(ProDetails details) {
        this.latitude = details.getUserDetails().getLocLatitude();
        this.longitude = details.getUserDetails().getLocLongitude();
        this.location = details.getUserDetails().getLocationText();
    }
}
