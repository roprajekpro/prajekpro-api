package com.prajekpro.api.domain;


import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import lombok.*;

import javax.persistence.*;
import java.text.*;
import java.util.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "appointment_invoice")
public class AppointmentInvoice extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @OneToOne()
    @JoinColumn(name = "FK_APPOINTMENT_ID")
    private AppointmentDetails appointmentDetails;

    @Column(name = "INVOICE_NO")
    private Integer invoiceNo;

    @Column(name = "INVOICE_TS")
    private Long invoiceTs;
}
