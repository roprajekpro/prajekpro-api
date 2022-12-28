package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ProServiceCommentsRepository extends JpaRepository<ProServiceComments, Long> {
    List<ProServiceComments> findByProDetails_IdAndService_Id(Long proId, Long serviceId);

    List<ProServiceComments> findByProDetails_IdAndService_IdAndActiveStatusOrderByModifiedTsDesc(Long proId, Long serviceId, int activeStatus);
}
