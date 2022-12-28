package com.prajekpro.api.domain;

import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
@Table(name = "admin_configuration")
public class AdminConfiguration extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "APPOINTMENT_CANCELLATION_PERCENT")
    private Long appointmentCancellationPer;

    @Column(name = "APPOINTMENT_COMPLETION_PRO_PERCENTAGE")
    private Long appointmentCompletionProPer;

    @Column(name = "APPOINTMENT_COMPLETION_PRAJEKPRO_PERCENT")
    private Long appointmentCompletionPrajekProPer;

    @OneToOne
    @JoinColumn(name = "ADMIN_USER_ID")
    private Users adminUser;

}
