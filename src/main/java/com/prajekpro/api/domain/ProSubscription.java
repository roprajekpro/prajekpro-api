package com.prajekpro.api.domain;


import com.prajekpro.api.enums.SubscriptionStatus;
import com.safalyatech.common.domains.Auditable;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString(callSuper = true)
@Table(name = "pro_subscription")
public class ProSubscription extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "FK_MASTER_SUBSCRIPTION_ID")
    private MasterSubscription masterSubscription;

    @Column(name = "DATE_OF_SUBSCRIPTION")
    private String dateOfSubscription;

    @Column(name = "TENURE")
    private Float tenure;

    @OneToOne
    @JoinColumn(name = "TRANSACTION_ID")
    @ToString.Exclude
    private WalletTransactionHistory walletTransactionHistory;

    @OneToMany(mappedBy = "proSubscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionInvoice> subscriptionInvoices;

    @Column(name = "SUBSCRIPTION_EXPIRES_ON")
    private String subscriptionExpiresOn;

    @OneToOne
    @JoinColumn(name = "FK_PAYMENT_ID")
    @ToString.Exclude
    private PaymentDetails paymentDetails;

    @ManyToOne
    @JoinColumn(name = "FK_PRO_ID")
    @ToString.Exclude
    private ProDetails proDetails;

    @Column(name = "STATUS")
    private SubscriptionStatus subscriptionStatus;

}
