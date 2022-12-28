package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ServiceItemCategory;
import com.safalyatech.common.repository.IBaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceItemCategoryRepository extends JpaRepository<ServiceItemCategory, Long>, IBaseRepository<ServiceItemCategory> {
    Page<ServiceItemCategory> findByServices_Id(Long id, Pageable pageable);

    Page<ServiceItemCategory> findByServices_IdAndActiveStatus(Long id, int value, Pageable pageable);


    List<ServiceItemCategory> findByServices_IdAndActiveStatus(Long id, int value);

    List<ServiceItemCategory> findByServices_Id(Long id);

    Page<ServiceItemCategory> findByServices_IdOrderByActiveStatus(Long id, Pageable pageable);
}
