package com.prajekpro.api.domain;

import com.prajekpro.api.enums.*;
import lombok.*;
import org.codehaus.jackson.annotate.*;

import javax.persistence.*;

@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
public class BaseDocument extends FileDetails {

    @Column(name = "DOC_TYPE")
    private DocType docType;

    @Column(name = "UPLOAD_DT")
    private Long uploadDt;

    @Transient
    @JsonIgnore
    public void updateDocumentDetails(DocType docType) {
        this.docType = docType;
        this.uploadDt = System.currentTimeMillis();
    }
}
