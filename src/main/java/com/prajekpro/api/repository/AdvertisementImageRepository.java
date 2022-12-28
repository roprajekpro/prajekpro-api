package com.prajekpro.api.repository;

import com.prajekpro.api.domain.AdvertisementImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementImageRepository extends JpaRepository<AdvertisementImage,Long> {
}
