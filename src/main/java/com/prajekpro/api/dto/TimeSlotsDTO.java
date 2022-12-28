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
public class TimeSlotsDTO {

    private Long id;
    private String storedValue;
    private String displayValue;
    private String description;
    private int isActive;

    public TimeSlotsDTO(TimeSlots appointmentTimeSlots) {
        this.id = appointmentTimeSlots.getId();
        this.storedValue = appointmentTimeSlots.getStoredValue();
        this.displayValue = appointmentTimeSlots.getDisplayValue();
        this.isActive = appointmentTimeSlots.getActiveStatus();
    }
}
