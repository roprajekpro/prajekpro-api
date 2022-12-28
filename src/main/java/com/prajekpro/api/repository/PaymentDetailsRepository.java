package com.prajekpro.api.repository;

import com.prajekpro.api.domain.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails,Long> {

   /* @Query(nativeQuery = true,value = "select pd.* from payment_details pd inner join wallet_transaction_history wth " +
            " on pd.ID = wth.FK_PAYMENT_DETAILS_ID inner join appointment_details ad " +
            " on wth.FK_APPOINTMENT_ID=ad.ID where ad.ID = :appointmentId ")*/
     @Query(nativeQuery = true,value = "select * from payment_details pd inner join wallet_transaction_history wth " +
             " on pd.ID = wth.FK_PAYMENT_DETAILS_ID inner join appointment_details ad " +
             " on wth.FK_APPOINTMENT_ID=ad.ID where ad.ID = :appointmentId and wth.MODIFIED_TS = (select max(wth1.MODIFIED_TS) from " +
             " wallet_transaction_history wth1 where wth1.FK_APPOINTMENT_ID = :appointmentId)")
    PaymentDetails getPaymentDetailsByAppointmentId(@Param("appointmentId") Long id);

    @Query(nativeQuery = true,value = "SELECT COALESCE(null,MAX(pd.TRANSACTION_ID)) from payment_details pd")
    Object fetchMaxTransactionId();

    @Query("select pd from PaymentDetails pd where pd.transactionId = :transactionId")
    PaymentDetails getPaymentDetailsByTxdId(@Param("transactionId") Integer transactionId);
}
