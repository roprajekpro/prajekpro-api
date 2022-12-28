package com.prajekpro.api.domain;

import com.safalyatech.common.domains.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "appointment_other_services")
public class AppointmentOtherServices extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_APPOINTMENT_ID")
    private AppointmentDetails appointmentDetails;

    @Column(name = "OTHER_SERVICE_NAME")
    private String serviceName;

    @Column(name = "REQ_QUANTITY")
    private  Long reqQuantity;

    @Column(name = "UNIT_PRICE")
    private Float unitPrice;
}
