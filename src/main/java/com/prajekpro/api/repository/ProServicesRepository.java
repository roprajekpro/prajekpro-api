package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ProServicesRepository extends JpaRepository<ProServices, Long> {
    Optional<ProServices> findByProIdAndServiceId(Long proId, Long serviceId);

    @Query(
            nativeQuery = true,
            value = "SELECT ps.* FROM pro_services ps " +
                    "INNER JOIN services s ON ps.fk_pro_services_service_id=s.ID " +
                    "WHERE fk_pro_services_pro_id=?1 AND s.ACTIVE_STATUS=?2"
    )
    List<ProServices> fetchProActiveServiceByProId(Long proId, int activeStatus);

    List<ProServices> findByProId(Long proId);

    List<ProServices> findByProIdInAndServiceId(List<Long> collect, Long serviceId);

    @Modifying
    @Query("update ProServices ps set ps.isCertified=:isCertified where ps.proId=:proId and ps.serviceId=:serviceId and ps.activeStatus=:activeStatus")
    void updateIsCertifiedByProIdAndServiceIdAndActiveStatus(@Param("isCertified") boolean isCertified, @Param("proId") Long proId,
                                                             @Param("serviceId") Long serviceId, @Param("activeStatus") int activeStatus);
}
