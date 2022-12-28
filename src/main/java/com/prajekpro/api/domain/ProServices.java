package com.prajekpro.api.domain;

import com.safalyatech.common.domains.*;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class ProServices extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRO_SERVICES_ID")
    private Long proServicesId;

    @Column(name = "fk_pro_services_pro_id")
    private Long proId;

    @Column(name = "fk_pro_services_service_id")
    private Long serviceId;

    @Column(name = "IS_PRAJEKPRO_VERIFIED")
    private boolean isPrajekproVerified;

    @Column(name = "IS_CERTIFIED")
    private boolean isCertified;

    public ProServices(Long proId, Long serviceId, boolean isCertified) {
        this.proId = proId;
        this.serviceId = serviceId;
        this.isCertified = isCertified;
    }
}
