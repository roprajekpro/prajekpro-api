package com.prajekpro.api.domain;


import com.prajekpro.api.enums.SubscriptionTypes;
import com.safalyatech.common.domains.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor

@Table(name = "master_subscription")

public class MasterSubscription extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SUBSCRIPTION_TYPE")
    private SubscriptionTypes subscriptionType;

    @Column(name = "SUBSCRPTION_AMOUNT")
    private Double subscriptionAmount;

    @Column(name = "SUBSCRIPTION_TENURE")
    private Float subscriptionTenure;

    @Column(name = "PRO_WALLET_PERCENTAGE")
    private Float proWalletPercentage;

    @Column(name = "PRAJEKPRO_WALLET_PERCENTAGE")
    private Float prajekProWalletPercentage;

    @Column(name = "PRO_ESSENTIAL_PERCENTAGE")
    private Float proEssentialPercentage;

    @Column(name = "LOOK_UP_TNC_REFERENCE")
    private String lookUpTncReference;

    @ManyToOne
    @JoinColumn(name = "FK_SUBSCRIPTION_CURRENCY_ID")
    private Currency currency;
}
