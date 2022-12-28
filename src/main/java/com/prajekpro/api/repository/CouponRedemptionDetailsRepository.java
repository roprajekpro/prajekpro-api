package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface CouponRedemptionDetailsRepository extends JpaRepository<CouponRedemptionDetails, Long> {
    List<CouponRedemptionDetails> findByCouponCodeDetails_IdAndUser_UserIdAndActiveStatus(Long id, String userId, int activeStatus);
}
