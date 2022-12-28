package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ProServiceTimeSlots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProServiceTimeSlotsRepository extends JpaRepository<ProServiceTimeSlots,Long> {

   @Query("select pst.timeSlots.id from ProServiceTimeSlots pst " +
           " where pst.proDetails.id=:proId and pst.services.id=:serviceId")
    List<Long> fetchByProIdAndServiceId(@Param("proId") Long proId, @Param("serviceId") Long serviceId);

   @Modifying
   @Query("DELETE FROM ProServiceTimeSlots pst " +
           "WHERE pst.proDetails.id = :proId and pst.services.id=:serviceId")
    int deleteByProIdAndServiceId(@Param("proId") Long proId, @Param("serviceId") Long serviceId);
}
