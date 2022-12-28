package com.prajekpro.api.domain;

import com.prajekpro.api.dto.WalletAddAmountDTO;
import com.prajekpro.api.enums.TransactionType;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.PPLookUp;
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
@Table(name = "wallet_top_up_history")
public class WalletTopUpHistory extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_WALLET_DETAILS_ID")
    private ProWalletDetails proWalletDetails;

    @ManyToOne
    @JoinColumn(name = "FK_PRO_ID")
    private ProDetails proDetails;

    @Column(name = "AMOUNT")
    private Double amount;

    @OneToOne
    @JoinColumn(name = "FK_SOURCE_ID")
    private PPLookUp ppLookUp;

    //TODO: added transaction from wallet transaction details
    @OneToOne
    @JoinColumn(name = "TRANSACTION_ID")
    private WalletTransactionHistory walletTransactionHistory;

    @Column(name = "TRANSACTION_TYPE")
    private TransactionType transactionType;

    public WalletTopUpHistory(WalletAddAmountDTO request, ProDetails proDetails, ProWalletDetails proWalletDetails) {
        this.proDetails = proDetails;
        this.amount = request.getAmount();
        this.ppLookUp = new PPLookUp(request.getSourceId());
        this.transactionType = TransactionType.LOAD_WALLET;
        this.proWalletDetails = proWalletDetails;
    }

    public WalletTopUpHistory(Long sourceId, Double amount, ProDetails proDetails, ProWalletDetails proWalletDetails) {
        this.proDetails = proDetails;
        this.amount = amount;
        this.ppLookUp = new PPLookUp(sourceId);
        /*        this.transactionType = TransactionType.LOAD_WALLET;*/
        this.proWalletDetails = proWalletDetails;
    }
}
