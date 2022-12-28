package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ServiceItemSubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceItemSubCategoryRepository extends JpaRepository<ServiceItemSubCategory, Long> {
    Page<ServiceItemSubCategory> findByServiceItemCategory_Id(Long categoryId, Pageable pageable);

    Page<ServiceItemSubCategory> findByServiceItemCategory_IdAndActiveStatus(Long categoryId, int value, Pageable pageable);

    List<ServiceItemSubCategory> findByServiceItemCategory_IdAndActiveStatus(Long id, int value);

    List<ServiceItemSubCategory> findByServiceItemCategory_Id(Long id);

    List<ServiceItemSubCategory> findByItemSubCategoryNameContainingIgnoreCaseAndActiveStatus(String term, int value);

    Page<ServiceItemSubCategory> findByServiceItemCategory_IdOrderByActiveStatus(Long categoryId, Pageable pageable);
}
