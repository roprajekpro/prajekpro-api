package com.prajekpro.api.repository;

import com.prajekpro.api.domain.Currency;
import com.prajekpro.api.domain.PrajekProWalletDetails;
import com.prajekpro.api.enums.WalletAmountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrajekproWalletDetailsRepository extends JpaRepository<PrajekProWalletDetails,Long> , JpaSpecificationExecutor<PrajekProWalletDetails> {

    @Query("select sum(ppw.amount) from PrajekProWalletDetails ppw where ppw.currency = :currency")
    Double getTotalPrajekProWalletAmount(@Param("currency") Currency currency);

    @Query("select sum(ppw.amount) from PrajekProWalletDetails ppw where ppw.walletAmountType = :amountType and ppw.currency = :currency ")
    Double getTotalAmountByAmountType(@Param("amountType") WalletAmountType amountType, @Param("currency") Currency currency);

    @Query("select distinct ppw.currency from PrajekProWalletDetails ppw ")
    List<Currency> getDistinctCurrency();

    @Query("select ppw from PrajekProWalletDetails ppw where ppw.walletTransactionHistory.transactionId = :txdId")
    List<PrajekProWalletDetails> getWalletDetailsByTxdId(@Param("txdId") Integer txdId);
}
