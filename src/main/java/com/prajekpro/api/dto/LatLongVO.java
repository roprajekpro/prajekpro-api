package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class LatLongVO {
    private Double latitude;
    private Double longitude;
}
