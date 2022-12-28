package com.prajekpro.api.domain;

import com.prajekpro.api.enums.TransactionType;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "wallet_transaction_history")
public class WalletTransactionHistory extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_WALLET_DETAILS_ID")
    private ProWalletDetails proWalletDetails;

    @ManyToOne
    @JoinColumn(name = "PRO_ID")
    private ProDetails proDetails;

    @OneToOne
    @JoinColumn(name = "PRO_SUBSCRIPTION_ID")
    private ProSubscription proSubscription;

    @OneToOne
    @JoinColumn(name = "FK_APPOINTMENT_ID")
    private AppointmentDetails appointmentDetails;

    //TODO:add customer mapping here
    @OneToOne
    @JoinColumn(name = "FK_CUSTOMER_ID")
    private Users customer;

    @Column(name = "TRANSACTION_ID")
    private Integer transactionId;

    @Column(name = "TRANSACTION_TYPE")
    private TransactionType transactionType;

    @Column(name = "AMOUNT")
    private Double amount;

    @OneToOne
    @JoinColumn(name = "FK_PAYMENT_DETAILS_ID")
    private PaymentDetails paymentDetails;


}
