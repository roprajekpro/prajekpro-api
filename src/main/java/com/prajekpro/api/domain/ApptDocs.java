package com.prajekpro.api.domain;

import com.prajekpro.api.enums.*;
import com.safalyatech.common.dto.*;
import lombok.*;
import org.codehaus.jackson.annotate.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "appt_docs")
public class ApptDocs extends BaseDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "FK_APPT_ID")
    private AppointmentDetails appointmentDetails;

    public ApptDocs(DocType docType, FileDetailsDTO fileDetailsDTO, AppointmentDetails appointmentDetails) {
        super.updateDocumentDetails(docType);

        super.updateFileDetails(fileDetailsDTO);

        this.appointmentDetails = appointmentDetails;
    }
}
