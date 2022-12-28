package com.prajekpro.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.prajekpro.api.domain.Services;
import com.safalyatech.common.repository.IBaseRepository;

import java.util.*;


@Repository
public interface ServicesRepository extends JpaRepository<Services, Long>, IBaseRepository<Services> {

    Page<Services> findAllByActiveStatus(int value, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "SELECT s.* " +
                    "FROM pro_services ps " +
                    "INNER JOIN services s on ps.fk_pro_services_service_id=s.id " +
                    "WHERE ps.fk_pro_services_pro_id IN (?1) and s.ACTIVE_STATUS IN (?2)"
    )
    Set<Services> fetchServicesByProIdIn(Long proId, List<Integer> activeStatus);

    Page<Services> findAllByOrderByActiveStatus(Pageable pageable);
}
