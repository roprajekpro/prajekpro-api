package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProServicesLocations;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProServicesLocationsDTO {
    public Long id;
    public double latitude;
    public double longitude;

    public ProServicesLocationsDTO(ProServicesLocations serviceLocation) {

        this.id = serviceLocation.getId();
        this.latitude = serviceLocation.getLatitude();
        this.longitude = serviceLocation.getLongitude();
    }
}
