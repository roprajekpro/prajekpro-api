package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class ServiceLocationDTO {

    private double latitude;
    private double longitude;
    private String priceOrder;
    private String term;
}
