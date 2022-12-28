package com.prajekpro.api.domain;

import com.prajekpro.api.enums.TransactionType;
import com.safalyatech.common.domains.Auditable;
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
@Table(name = "payment_details")
public class PaymentDetails extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_PRO_ID")
    @ToString.Exclude
    private ProDetails proDetails;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;

    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus;

    @Column(name = "TRANSACTION_ID")
    private Integer transactionId;

    @Column(name = "TRANSACTION_TYPE")
    private TransactionType transactionType;

    @Column(name = "FINAL_RESULT_CODE")
    private String finalResultCode;

    @Column(name = "REDIRECT_RESULT_CODE")
    private String redirectResultCode;

    @Column(name = "PSP_REFERENCE")
    private String pspReference;

    @Column(name = "METADATA")
    private String metaData;

    @Column(name = "CHECKOUT_URL")
    private String checkoutUrl;

    public PaymentDetails(ProDetails proDetails, String paymentMethodType, String paymentStatus,
                          int transactionId, TransactionType transactionType, String redirectResultCode,
                          boolean isCreate, String userId, int activeStatus, String checkOutUrl) {
        this.proDetails = proDetails;
        this.paymentMethod = paymentMethodType;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.redirectResultCode = redirectResultCode;
        this.checkoutUrl = checkOutUrl;
        updateAuditableFields(isCreate, userId, activeStatus);
    }
}