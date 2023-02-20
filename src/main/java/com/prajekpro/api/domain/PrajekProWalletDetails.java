package com.prajekpro.api.domain;


import com.prajekpro.api.enums.*;
import com.safalyatech.common.domains.*;
import lombok.*;

import javax.persistence.*;
import java.io.*;


@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "prajekpro_wallet_details")
public class PrajekProWalletDetails extends Auditable implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "FK_PRO_ID")
    private ProDetails proDetails;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "AMOUNT_TYPE")
    private WalletAmountType walletAmountType;

    @ManyToOne
    @JoinColumn(name = "FK_CURRENCY_ID")
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "TRANSACTION_ID")
    private WalletTransactionHistory walletTransactionHistory;

    public PrajekProWalletDetails(ProDetails proDetails, Double prajekProWalletAmount, WalletAmountType walletAmountType,
                                  Currency currency, WalletTransactionHistory walletTransactionHistory) {
        this.proDetails = proDetails;
        this.amount = prajekProWalletAmount!=null?prajekProWalletAmount:0.0;
        this.walletAmountType = walletAmountType;
        this.currency = currency;
        this.walletTransactionHistory = walletTransactionHistory;
    }

   /* @OneToMany(targetEntity = WalletTransactionHistory.class,mappedBy = "walletDetails")
    private List<WalletTransactionHistory> walletTransactionHistoryList;

    @OneToMany(targetEntity = WalletTopUpHistory.class, mappedBy = "walletDetails",cascade = CascadeType.ALL)
    private List<WalletTopUpHistory> walletTopUpHistoryList;*/
}
