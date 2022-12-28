package com.prajekpro.api.domain;

import com.safalyatech.common.domains.*;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class ProServiceComments extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_PRO_ID")
    private ProDetails proDetails;

    @ManyToOne
    @JoinColumn(name = "FK_SERVICE_ID")
    private Services service;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "COMMENT")
    private String comment;

    public ProServiceComments(Long proId, Long serviceId, String comment, String subject) {
        this.proDetails = new ProDetails(proId);
        this.service = new Services(serviceId);
        this.subject = subject;
        this.comment = comment;
    }
}
