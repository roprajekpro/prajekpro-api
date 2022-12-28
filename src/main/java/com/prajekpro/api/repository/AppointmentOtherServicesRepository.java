package com.prajekpro.api.repository;

import com.prajekpro.api.domain.AppointmentOtherServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentOtherServicesRepository extends JpaRepository<AppointmentOtherServices,Long> {
}
