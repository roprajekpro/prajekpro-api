package com.prajekpro.api.domain;

import com.safalyatech.common.domains.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "pro_service_time_slots")
public class ProServiceTimeSlots extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_PRO_ID")
    private ProDetails proDetails;

    @ManyToOne
    @JoinColumn(name = "FK_SERVICE_ID")
    private Services services;

    @ManyToOne
    @JoinColumn(name = "FK_TIME_SLOT_ID")
    private TimeSlots timeSlots;

    public ProServiceTimeSlots(ProDetails proDetails, Services service, TimeSlots timeSlots) {
        this.proDetails = proDetails;
        this.services = service;
        this.timeSlots = timeSlots;
    }
}
