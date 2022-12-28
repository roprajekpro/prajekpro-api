package com.prajekpro.api.repository;

import com.prajekpro.api.domain.AppointmentInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentInvoiceRepository extends JpaRepository<AppointmentInvoice,Long> {

    @Query(nativeQuery = true, value = "select COALESCE(null,max(INVOICE_NO)) from appointment_invoice")
    Integer fetchMaxInvoiceNo();

    @Query("select ai from AppointmentInvoice ai where ai.appointmentDetails.id = :appointmentId")
    AppointmentInvoice findByAppointmentId(@Param("appointmentId") Long appointmentId);
}
