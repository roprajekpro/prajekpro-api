package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AppointmentSlotsDTO {

    private long serviceId;
    private long proId;
    private String date;
    private List<CommonFieldsDTO> timeSlots;
}
