package com.prajekpro.api.dto;

import com.prajekpro.api.enums.AppointmentState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentSearchRequestBodyDTO {

    private String startDate;
    private String endDate;
    private List<ServicesDTO> services;
    private List<AppointmentState> state;
    private List<LatLongVO> latLong;
    private List<Long> proId;
    private List<String> customerId;
}
