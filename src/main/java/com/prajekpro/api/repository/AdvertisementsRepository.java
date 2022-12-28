package com.prajekpro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prajekpro.api.domain.Advertisements;
import com.safalyatech.common.repository.IBaseRepository;

@Repository
public interface AdvertisementsRepository extends JpaRepository<Advertisements, Long>, IBaseRepository<Advertisements> {

}
