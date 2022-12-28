package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import com.prajekpro.api.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ProSubscriptionRepository extends JpaRepository<ProSubscription, Long> {

    @Query("select ps from ProSubscription ps where ps.proDetails.id = ?1 ")
    List<ProSubscription> findByProId(Long id, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "select ps.* from pro_subscription ps where ps.FK_PRO_ID=:proId and ps.ACTIVE_STATUS=:status ORDER BY ps.CREATED_TS DESC LIMIT 1"
    )
    ProSubscription findByProIdAndModifiedTs(@Param("proId") Long id, @Param("status") int value);

    /*@Query("SELECT MAX(ps.invoice) from ProSubscription ps")
    int fetchMaxInvoiceNo();*/

    @Query(
            nativeQuery = true,
            value = "select ps.STATUS from pro_subscription ps " +
                    " where ps.FK_PRO_ID = :proId and ps.ACTIVE_STATUS=:status ORDER BY ps.CREATED_TS DESC LIMIT 1"
    )
    SubscriptionStatus findSubscriptionStatusByProId(@Param("proId") Long proId, @Param("status") int value);

    @Query("select ps from ProSubscription ps where ps.proDetails.id = ?1 ")
    List<ProSubscription> getSubscriptionList(Long proId);

    @Query("select ps from ProSubscription ps where ps.walletTransactionHistory.transactionId = :txdId")
    ProSubscription getSubscriptionByTXDId(@Param("txdId") Integer txdId);

    @Query(nativeQuery = true, value = "select * from pro_subscription ps where ps.FK_PRO_ID = :proId " +
            " and ps.ACTIVE_STATUS=:status ORDER BY ps.CREATED_TS DESC LIMIT 1")
    ProSubscription getSubscriptionByProId(@Param("proId") Long proId, @Param("status") int value);
    @Query("select ps from ProSubscription ps where ps.proDetails.id = ?1 and ps.activeStatus=?2")
    List<ProSubscription> findByProIdAndActiveStatus(Long id, int activeStatus, Pageable pageable);
}
