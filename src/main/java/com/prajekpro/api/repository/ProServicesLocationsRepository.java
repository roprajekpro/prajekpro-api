package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ProServicesLocations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProServicesLocationsRepository extends JpaRepository<ProServicesLocations,Long> {
    List<ProServicesLocations> findAllByProDetails_Id(Long id);
}
