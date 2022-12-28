package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ProAvailableDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProAvailableDayRepository extends JpaRepository<ProAvailableDay, Long> {

    @Query("SELECT pd.availableDays.id from ProAvailableDay pd where pd.proDetails.id = :proId " +
            " and pd.services.id = :serviceId")
    List<Long> fetchByProIdAndServiceId(@Param("proId") Long proId, @Param("serviceId") Long serviceId);

    @Modifying
    @Query("DELETE FROM ProAvailableDay pad " +
            "WHERE pad.proDetails.id = :proId and pad.services.id=:serviceId")
    int deleteByProAndService(@Param("proId") Long proId, @Param("serviceId") Long serviceId);

    @Query("SELECT pd from ProAvailableDay pd where pd.proDetails.id = :proId " +
            " and pd.services.id = :serviceId")
    List<ProAvailableDay> fetchDaysByProAndService(@Param("proId") Long proId, @Param("serviceId") Long serviceId);
}
