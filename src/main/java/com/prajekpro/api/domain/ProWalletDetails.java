package com.prajekpro.api.domain;

import com.safalyatech.common.domains.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "pro_wallet_details")
public class ProWalletDetails extends Auditable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @ToString.Exclude
    @JoinColumn(name = "FK_PRO_ID")
    private ProDetails proDetails;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "LOCKED_AMOUNT")
    private Double lockedAmount;

    @ToString.Exclude
    @OneToMany(targetEntity = WalletTransactionHistory.class, mappedBy = "proWalletDetails")
    private List<WalletTransactionHistory> walletTransactionHistoryList;

    @ToString.Exclude
    @OneToMany(targetEntity = WalletTopUpHistory.class, mappedBy = "proWalletDetails")
    private List<WalletTopUpHistory> walletTopUpHistoryList;
}
