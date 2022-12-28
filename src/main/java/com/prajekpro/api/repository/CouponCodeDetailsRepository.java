package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface CouponCodeDetailsRepository extends JpaRepository<CouponCodeDetails, Long> {
    CouponCodeDetails findByCouponCodeIgnoreCaseAndActiveStatus(String couponCode, int activeStatus);
}
