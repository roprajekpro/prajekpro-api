package com.prajekpro.api.repository;

import com.prajekpro.api.domain.CommercialAdvertisement;
import com.safalyatech.common.repository.IBaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommercialAdvertisementRepository extends IBaseRepository<CommercialAdvertisement>, JpaRepository<CommercialAdvertisement, Long> {
}
