package com.prajekpro.api.repository;

import com.prajekpro.api.domain.AppointmentRequestedServiceSubCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRequestedServiceSubCategoriesRepository extends JpaRepository<AppointmentRequestedServiceSubCategories,Long> {
}
