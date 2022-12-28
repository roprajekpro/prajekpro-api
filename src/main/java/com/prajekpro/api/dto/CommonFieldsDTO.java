package com.prajekpro.api.dto;

import com.prajekpro.api.domain.TimeSlots;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommonFieldsDTO {
    private Long id;
    private String value;
    private boolean enabled;

    public CommonFieldsDTO(Long timeSlotId, String appointmentTime) {
        this.id = timeSlotId;
        this.value = appointmentTime;
        this.enabled = true;
    }

    public CommonFieldsDTO(TimeSlots timeSlots) {
        this.id = timeSlots.getId();
        this.value = timeSlots.getDisplayValue();
        this.enabled = true;
    }


}
