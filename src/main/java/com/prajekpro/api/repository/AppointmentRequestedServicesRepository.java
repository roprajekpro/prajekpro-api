package com.prajekpro.api.repository;

import com.prajekpro.api.domain.AppointmentRequestedServices;
import com.prajekpro.api.enums.AppointmentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface AppointmentRequestedServicesRepository extends JpaRepository<AppointmentRequestedServices, Long> {

    @Query("select ars.timeSlotId from AppointmentRequestedServices ars " +
            " inner join AppointmentDetails ad on ars.appointmentDetails.id = ad.id "+
            " AND ars.appointmentDate =:date  " +
            " AND ad.bookedFor.id=:proId AND ad.state NOT IN (:appointmentState)")
    List<Long> getTimeSlotId(@Param("proId") Long proId, @Param("date") String date, @Param("appointmentState") List<AppointmentState> appointmentState) ;


    @Query("select count(ars) " +
            "from AppointmentRequestedServices ars " +
            "inner join AppointmentDetails ad on ars.appointmentDetails.id = ad.id " +
            " AND ars.appointmentDate =:appointmentDate  AND ars.timeSlotId=:appointmentTimeId " +
            "AND ad.bookedFor.id=:bookedFor AND ad.state <> :appointmentState  ")
    Integer countProBookedAppointmentsByDateAndTimeAndServiceId(@Param("bookedFor") Long bookedFor, @Param("appointmentDate") String appointmentDate, @Param("appointmentTimeId") Long appointmentTimeId, @Param("appointmentState") AppointmentState appointmentState);
}
