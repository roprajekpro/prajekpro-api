package com.prajekpro.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prajekpro.api.dto.Detail;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.PPLookUp;
import lombok.*;
import org.codehaus.jackson.annotate.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "services")
public class Services extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    @Column(name = "SERVICE_NAME")
    @NotBlank(message = "Service Name is Mandatory")
    private String serviceName;

    @Column(name = "SERVICE_ICON")
    private String serviceIcon;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CANCELLATION_TIME")
    private Long cancellationTime;

    @Column(name = "CANCELLATION_FEES")
    private Long cancellationFees;

    @ManyToOne
    @JoinColumn(name = "CANCELLATION_TIME_UNIT")
    private PPLookUp cancellationTimeUnit;

    @ManyToOne
    @JoinColumn(name = "CANCELLATION_FEES_UNIT")
    private PPLookUp cancellationFeesUnit;


    @OneToMany(targetEntity = ServiceItemCategory.class,mappedBy = "services",cascade = CascadeType.ALL)
    private List<ServiceItemCategory> serviceItemCategories;

    //Transient properties
    @Transient
    @JsonProperty(value = "isCertified")
    private boolean isCertified;

    public Services(Long id) {
        this.id = id;
    }

    @Transient
    public Detail getDetail() {

        return new Detail(id, serviceName, serviceIcon);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Services))
            return false;
        if (obj == this)
            return true;

        Services services = (Services) obj;
        return this.id == services.getId();
    }
}
