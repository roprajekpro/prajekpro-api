package com.prajekpro.api.repository;

import com.prajekpro.api.domain.AdminConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminConfigurationRepository extends JpaRepository<AdminConfiguration,Long> {
}
