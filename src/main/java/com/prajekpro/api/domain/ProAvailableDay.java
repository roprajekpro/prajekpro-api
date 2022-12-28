package com.prajekpro.api.domain;

import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.PPLookUp;
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
@Table(name = "pro_available_day")
public class ProAvailableDay extends Auditable {

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
    @JoinColumn(name = "FK_AVAILABLE_DAY")
    private PPLookUp availableDays;

    public ProAvailableDay(ProDetails proDetails, Services service, PPLookUp day) {
        this.proDetails = proDetails;
        this.services = service;
        this.availableDays = day;
    }
}
