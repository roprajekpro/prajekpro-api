package com.prajekpro.api.dto;

import com.prajekpro.api.domain.TimeSlots;
import com.safalyatech.common.domains.PPLookUp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProServiceScheduleDTO {
    private List<CommonFieldsDTO> availableDays = new ArrayList<>();
    private List<CommonFieldsDTO> availableTimeSlots = new ArrayList<>();

    public ProServiceScheduleDTO(List<Long> proAvailableDays, List<PPLookUp> days,
                                 List<Long> proServiceTimeSlots, List<TimeSlots> timeSlots) {

        for (PPLookUp day : days) {
            CommonFieldsDTO availableDay = new CommonFieldsDTO();
            availableDay.setId(day.getId());
            availableDay.setValue(day.getValue());
            if (proAvailableDays.contains(day.getId())) {
                availableDay.setEnabled(true);
            } else {
                availableDay.setEnabled(false);
            }
            this.availableDays.add(availableDay);
        }

        for (TimeSlots slots : timeSlots) {
            CommonFieldsDTO availableTimeSlot = new CommonFieldsDTO();
            availableTimeSlot.setId(slots.getId());
            availableTimeSlot.setValue(slots.getDisplayValue());
            if (proServiceTimeSlots.contains(slots.getId())) {
                availableTimeSlot.setEnabled(true);
            } else {
                availableTimeSlot.setEnabled(false);
            }
            this.availableTimeSlots.add(availableTimeSlot);
        }


    }
}
