package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ProServiceItemsPricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProServiceItemsPricingRepository extends JpaRepository<ProServiceItemsPricing,Long> {

    List<ProServiceItemsPricing> findByProDetails_Id(Long id);

    List<ProServiceItemsPricing> findByProDetails_IdAndServices_Id(Long proId, Long serviceId);

/*
    int deleteByProDetails_IdAndserviceItemSubcategory_IdNotIn(Long proId, List<Long> subCategoryId);
*/

    @Modifying
    @Query("DELETE FROM ProServiceItemsPricing psp WHERE psp.proDetails.id = :proId and psp.id Not In (:servicePricingId) and psp.services.id = :serviceId")
    int deleteByIdNotInANDProDetails(@Param("servicePricingId") List<Long> servicePricingId, @Param("proId") Long proId, @Param("serviceId") Long serviceId);

    @Modifying
    @Query("DELETE FROM ProServiceItemsPricing psp WHERE psp.id In (:servicePricingId)")
    int deleteRecordByIds(@Param("servicePricingId") List<Long> servicePricingId);

    @Query(" select min(psp.price) from ProServiceItemsPricing psp" +
            " where psp.services.id=:serviceId and psp.proDetails.id = :proId")
    Long fetchMinPriceByProIdAndServiceId(@Param("proId") Long proId, @Param("serviceId") Long serviceId);

    @Modifying
    @Query("DELETE FROM ProServiceItemsPricing psp WHERE psp.proDetails.id = :proId and psp.services.id In (:existingServiceIDs)")
    void deleteByServiceAndProDetails(@Param("existingServiceIDs") List<Long> existingServiceIDs, @Param("proId") Long proId);
}
