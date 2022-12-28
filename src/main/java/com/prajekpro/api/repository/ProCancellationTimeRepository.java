package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ProCancellationTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProCancellationTimeRepository extends JpaRepository<ProCancellationTime,Long> {
   @Query(" select pct from ProCancellationTime pct " +
           " where pct.proDetails.id = :proId and pct.services.id = :serviceId")
   ProCancellationTime fetchTimeByProIdAndServiceId(@Param("proId") Long proId,@Param("serviceId") Long serviceId);
}
