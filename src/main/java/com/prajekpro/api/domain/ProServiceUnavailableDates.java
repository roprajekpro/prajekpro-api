package com.prajekpro.api.domain;

import com.safalyatech.common.domains.*;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Setter
@Getter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "pro_service_unavailable_dates")
public class ProServiceUnavailableDates extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "FK_PRO_ID")
    private ProDetails proDetails;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "FK_SERVICE_ID")
    private Services service;

    @Column(name = "START_DT")
    private String startDt;

    @Column(name = "END_DT")
    private String endDt;

    public ProServiceUnavailableDates(Long proId, Long serviceId, ProServiceUnavailableDates request) {
        this.proDetails = new ProDetails(proId);
        this.service = new Services(serviceId);
        this.startDt = request.getStartDt();
        this.endDt = request.getEndDt();
    }
}
