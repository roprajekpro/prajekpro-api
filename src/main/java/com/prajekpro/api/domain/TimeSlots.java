package com.prajekpro.api.domain;

import com.prajekpro.api.dto.TimeSlotsDTO;
import com.safalyatech.common.domains.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "time_slots")
public class TimeSlots extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "STORED_VALUE")
    private String storedValue;

    @Column(name = "DISPLAY_VALUE")
    private String displayValue;

    @Column(name = "DESCRIPTION")
    private String description;

    public TimeSlots(TimeSlotsDTO timeSlot) {
        this.storedValue = timeSlot.getStoredValue();
        this.displayValue = timeSlot.getDisplayValue();
        this.description = timeSlot.getDescription();
    }

    public TimeSlots(Long id, String value) {
        this.id = id;
        this.displayValue = value;
    }
}
