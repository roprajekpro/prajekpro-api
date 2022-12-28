package com.prajekpro.api.repository;

import com.prajekpro.api.domain.SubscriptionInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionInvoiceRepository extends JpaRepository<SubscriptionInvoice,Long> {

    @Query(nativeQuery = true,value = "SELECT COALESCE(null,MAX(si.INVOICE_NO)) from subscription_invoice si")
    int fetchMaxInvoiceNo();

    @Query("select si from SubscriptionInvoice si where si.proSubscription.id = :id")
    SubscriptionInvoice getByProSubscriptionId(@Param("id") Long id);
}