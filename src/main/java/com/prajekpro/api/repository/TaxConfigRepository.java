package com.prajekpro.api.repository;

import com.prajekpro.api.domain.TaxConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxConfigRepository extends JpaRepository<TaxConfig,Long> {
    List<TaxConfig> findByActiveStatus(int value);
}
