package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ProServiceUnavailableDatesRepository extends JpaRepository<ProServiceUnavailableDates, Long> {
    List<ProServiceUnavailableDates> findByProDetails_IdAndService_IdAndActiveStatusOrderByStartDtAsc(Long proId, Long serviceId, int activeStatus);

    ProServiceUnavailableDates findByProDetails_IdAndService_IdAndStartDtAndEndDt(Long proId, Long serviceId, String startDt, String endDt);

    List<ProServiceUnavailableDates> findByProDetails_IdAndService_IdAndStartDtLessThanEqualAndEndDtGreaterThanEqualAndActiveStatusIn(Long proId, Long serviceId, String date, String date1, List<Integer> asList);
}
