package com.prajekpro.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prajekpro.api.enums.FileTypes;
import com.safalyatech.common.domains.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@Table(name = "pro_documents")
public class ProDocuments extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "FK_PRO_DOCUMENTS_PRO_ID")
    private ProDetails proDetails;

    private FileTypes type;
    private String url;
    private String name;


    @Override
    public int hashCode() {
        return this.type.hashCode() + this.url.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof ProDocuments))
            return false;
        if (obj == this)
            return true;

        ProDocuments proDocument = (ProDocuments) obj;
        return this.type == proDocument.getType() && this.url.equals(proDocument.getUrl());
    }
}
