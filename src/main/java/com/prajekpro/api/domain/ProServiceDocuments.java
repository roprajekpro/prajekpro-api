package com.prajekpro.api.domain;

import com.prajekpro.api.enums.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import lombok.*;
import org.codehaus.jackson.annotate.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "pro_service_documents")
public class ProServiceDocuments extends BaseDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_PRO_ID")
    private ProDetails proDetails;

    @ManyToOne
    @JoinColumn(name = "FK_SERVICE_ID")
    private Services service;

    public ProServiceDocuments(ProDetails proDetails, Services service) {
        this.proDetails = proDetails;
        this.service = service;
    }
}
