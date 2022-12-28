package com.prajekpro.api.repository;

import com.prajekpro.api.domain.WalletTopUpHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTopUpHistoryRepository extends JpaRepository<WalletTopUpHistory, Long> {
    @Query("select tuh from WalletTopUpHistory tuh where tuh.proWalletDetails.id = ?1 and tuh.activeStatus=?2 order by tuh.createdTs DESC")
    Page<WalletTopUpHistory> findAllByProWalletDetails_Id(Long id, int activeStatus, Pageable pageable);

    @Query("select wth from WalletTopUpHistory wth where wth.walletTransactionHistory.transactionId = :txdId")
    WalletTopUpHistory getTopupHistoryByTxdId(@Param("txdId") Integer txdId);
}
