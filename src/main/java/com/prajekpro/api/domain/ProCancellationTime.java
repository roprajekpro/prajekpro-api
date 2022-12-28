package com.prajekpro.api.domain;

import com.prajekpro.api.dto.ProCancellationTimeDTO;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.PPLookUp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "pro_cancellation_time")
public class ProCancellationTime extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_PRO_ID")
    private ProDetails proDetails;

    @ManyToOne
    @JoinColumn(name = "FK_SERVICE_ID")
    private Services services;

    @Column(name = "CANCELLATION_TIME")
    private Long cancellationTime;

    @ManyToOne
    @JoinColumn(name = "FK_CANCELLATION_UNIT_ID")
    private PPLookUp cancellationTimeUnit;

    public ProCancellationTime(ProDetails proDetails, ProCancellationTimeDTO request) {
        this.proDetails = proDetails;
        this.cancellationTime = request.getCancellationTime();
        this.services = new Services(request.getServiceId());
        this.cancellationTimeUnit = new PPLookUp(request.getCancellationTimeUnitId());
    }
}
