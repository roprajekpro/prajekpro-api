package com.prajekpro.api.domain;

import com.safalyatech.common.domains.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subscription_invoice")
public class SubscriptionInvoice extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_PRO_SUBSCRIPTION_ID")
    private ProSubscription proSubscription;

    @Column(name = "INVOICE_NO")
    private Integer invoiceNo;

    @Column(name = "INVOICE_TS")
    private Long invoiceTs;
}
