package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ProReviews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProReviewsRepository extends JpaRepository<ProReviews,Long> {

    List<ProReviews> findByProDetails_Id(Long proId, Pageable pageable);

    Page<ProReviews> findByCustomer_UserId(String userId, Pageable pageable);

    List<ProReviews> findByProDetails_Id(Long proId);
}
