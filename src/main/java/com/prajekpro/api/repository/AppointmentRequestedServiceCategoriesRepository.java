package com.prajekpro.api.repository;

import com.prajekpro.api.domain.AppointmentRequestedServiceCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRequestedServiceCategoriesRepository extends JpaRepository<AppointmentRequestedServiceCategories, Long> {
}
