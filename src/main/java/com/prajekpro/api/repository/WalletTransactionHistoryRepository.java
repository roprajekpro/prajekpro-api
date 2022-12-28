package com.prajekpro.api.repository;

import com.prajekpro.api.domain.WalletTransactionHistory;
import com.prajekpro.api.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionHistoryRepository extends JpaRepository<WalletTransactionHistory,Long> {
    @Query("select th from WalletTransactionHistory th where th.proWalletDetails.id = ?1 and th.transactionType in (?2) and th.activeStatus=?3 order by th.createdTs DESC")
    Page<WalletTransactionHistory> findAllByProWalletDetails_Id(Long id, List<TransactionType> transactionType, int activeStatus, Pageable pageable);

    @Query(nativeQuery = true,value = "SELECT COALESCE(null,MAX(wth.TRANSACTION_ID)) from wallet_transaction_history wth")
    int fetchMaxTransactionId();

    @Query("select wth from WalletTransactionHistory wth where wth.transactionId = :txdId")
    WalletTransactionHistory getTransactionHistoryByTXDId(@Param("txdId") Integer txdId);
}
