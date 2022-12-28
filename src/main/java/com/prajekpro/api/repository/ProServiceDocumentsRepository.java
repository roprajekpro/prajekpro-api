package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import com.prajekpro.api.enums.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ProServiceDocumentsRepository extends JpaRepository<ProServiceDocuments, Long> {
    List<ProServiceDocuments> findByProDetails_IdAndService_IdAndActiveStatusNotIn(Long proId, Long serviceId, List<Integer> activeStatusList);

    List<ProServiceDocuments> findByProDetails_IdAndService_Id(Long proId, Long serviceId, Pageable pageable);

    Optional<ProServiceDocuments> findByProDetails_IdAndService_Id(Long proId, Long serviceId);

    List<ProServiceDocuments> findByProDetails_IdAndService_IdIn(Long proId, List<Long> serviceIds);

    List<ProServiceDocuments> findByProDetails_IdAndDocTypeAndService_IdIn(Long proId, DocType docType, Set<Long> serviceIds);

    Integer countByProDetails_IdAndService_IdAndDocTypeAndActiveStatus(Long proId, Long serviceId, DocType certNLicenses, int activeStatus);
}
