package com.prajekpro.api.repository;

import com.prajekpro.api.domain.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ProDetailsRepository extends JpaRepository<ProDetails, Long>, IBaseRepository<ProDetails>, JpaSpecificationExecutor<ProDetails> {

    //    @Query(
//            nativeQuery = true,
//            value = "SELECT DISTINCT " +
//                    " ACOS( COS( RADIANS( :lat ) ) * COS( RADIANS( u.LATITUDE ) ) * COS( RADIANS( u.LONGITUDE ) - RADIANS( :lng ) ) + SIN( RADIANS( :lat )) * SIN( RADIANS( u.LATITUDE ))) as DISTANCE_IN_KMS, " +
//                    " ps.fk_pro_services_service_id, psc.STATUS, pwd.AMOUNT, pd.* " +
//                    " FROM pro_details pd " +
//                    " JOIN pro_services ps ON pd.ID=ps.fk_pro_services_pro_id AND ps.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN users u ON pd.fk_pro_details_user_id=u.USER_ID AND u.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN pro_subscription psc ON pd.ID=psc.FK_PRO_ID AND psc.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN pro_wallet_details pwd ON pwd.FK_PRO_ID=pd.ID AND pwd.ACTIVE_STATUS=:activeStatus " +
//                    " HAVING " +
//                    " ps.fk_pro_services_service_id IN (:serviceIds) " +
//                    " AND psc.STATUS = :subscriptionStatus " +
//                    " AND DISTANCE_IN_KMS < :serviceLocationRadius " +
//                    " AND pd.AVAILABILITY_STATUS IN (:availabilityStatus) " +
//                    " AND pwd.AMOUNT >= :thresholdValue"
//    )
    @Query(
            nativeQuery = true,
            value = "SELECT DISTINCT " +
                    " (ST_Distance_Sphere(point(:lng,:lat),point(u.LONGITUDE,u.LATITUDE)) * 0.001) as DISTANCE_IN_KMS, " +
                    " ps.fk_pro_services_service_id, psc.STATUS, pwd.AMOUNT, pd.* " +
                    " FROM pro_details pd " +
                    " JOIN pro_services ps ON pd.ID=ps.fk_pro_services_pro_id AND ps.ACTIVE_STATUS=:activeStatus " +
                    " JOIN users u ON pd.fk_pro_details_user_id=u.USER_ID AND u.ACTIVE_STATUS=:activeStatus " +
                    " JOIN pro_subscription psc ON pd.ID=psc.FK_PRO_ID AND psc.ACTIVE_STATUS=:activeStatus " +
                    " JOIN pro_wallet_details pwd ON pwd.FK_PRO_ID=pd.ID AND pwd.ACTIVE_STATUS=:activeStatus " +
                    " HAVING " +
                    " ps.fk_pro_services_service_id IN (:serviceIds) " +
                    " AND psc.STATUS = :subscriptionStatus " +
                    " AND DISTANCE_IN_KMS < :serviceLocationRadius " +
                    " AND pd.AVAILABILITY_STATUS IN (:availabilityStatus) " +
                    " AND pwd.AMOUNT >= :thresholdValue"
    )
    List<ProDetails> fetchByProListingCriterias(@Param("lat") double lat, @Param("lng") double lng, @Param("activeStatus") int activeStatus,
                                                @Param("serviceIds") List<Long> serviceIds, @Param("subscriptionStatus") int subscriptionStatus,
                                                @Param("serviceLocationRadius") float serviceLocationRadius, @Param("availabilityStatus") int availabilityStatus,
                                                @Param("thresholdValue") float thresholdValue, Pageable pageable);

    //    @Query(
//            nativeQuery = true,
//            value = "SELECT COUNT(*) FROM (SELECT DISTINCT " +
//                    " ACOS( COS( RADIANS( :lat ) ) * COS( RADIANS( u.LATITUDE ) ) * COS( RADIANS( u.LONGITUDE ) - RADIANS( :lng ) ) + SIN( RADIANS( :lat )) * SIN( RADIANS( u.LATITUDE ))) as DISTANCE_IN_KMS, " +
//                    " ps.fk_pro_services_service_id, psc.STATUS, pwd.AMOUNT, pd.* " +
//                    " FROM pro_details pd " +
//                    " JOIN pro_services ps ON pd.ID=ps.fk_pro_services_pro_id AND ps.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN users u ON pd.fk_pro_details_user_id=u.USER_ID AND u.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN pro_subscription psc ON pd.ID=psc.FK_PRO_ID AND psc.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN pro_wallet_details pwd ON pwd.FK_PRO_ID=pd.ID AND pwd.ACTIVE_STATUS=:activeStatus " +
//                    " HAVING " +
//                    " ps.fk_pro_services_service_id IN (:serviceIds) " +
//                    " AND psc.STATUS = :subscriptionStatus " +
//                    " AND DISTANCE_IN_KMS < :serviceLocationRadius " +
//                    " AND pd.AVAILABILITY_STATUS IN (:availabilityStatus) " +
//                    " AND pwd.AMOUNT >= :thresholdValue ) AS A"
//    )
    @Query(
            nativeQuery = true,
            value = "SELECT COUNT(*) FROM (SELECT DISTINCT " +
                    " (ST_Distance_Sphere(point(:lng,:lat),point(u.LONGITUDE,u.LATITUDE)) * 0.001) as DISTANCE_IN_KMS, " +
                    " ps.fk_pro_services_service_id, psc.STATUS, pwd.AMOUNT, pd.* " +
                    " FROM pro_details pd " +
                    " JOIN pro_services ps ON pd.ID=ps.fk_pro_services_pro_id AND ps.ACTIVE_STATUS=:activeStatus " +
                    " JOIN users u ON pd.fk_pro_details_user_id=u.USER_ID AND u.ACTIVE_STATUS=:activeStatus " +
                    " JOIN pro_subscription psc ON pd.ID=psc.FK_PRO_ID AND psc.ACTIVE_STATUS=:activeStatus " +
                    " JOIN pro_wallet_details pwd ON pwd.FK_PRO_ID=pd.ID AND pwd.ACTIVE_STATUS=:activeStatus " +
                    " HAVING " +
                    " ps.fk_pro_services_service_id IN (:serviceIds) " +
                    " AND psc.STATUS = :subscriptionStatus " +
                    " AND DISTANCE_IN_KMS < :serviceLocationRadius " +
                    " AND pd.AVAILABILITY_STATUS IN (:availabilityStatus) " +
                    " AND pwd.AMOUNT >= :thresholdValue ) AS A"
    )
    Integer countByProListingCriterias(@Param("lat") double lat, @Param("lng") double lng, @Param("activeStatus") int activeStatus,
                                       @Param("serviceIds") List<Long> serviceIds, @Param("subscriptionStatus") int subscriptionStatus,
                                       @Param("serviceLocationRadius") float serviceLocationRadius, @Param("availabilityStatus") int availabilityStatus,
                                       @Param("thresholdValue") float thresholdValue);

    //    @Query(
//            nativeQuery = true,
//            value = "SELECT DISTINCT " +
//                    " ACOS( COS( RADIANS( :lat ) ) * COS( RADIANS( u.LATITUDE ) ) * COS( RADIANS( u.LONGITUDE ) - RADIANS( :lng ) ) + SIN( RADIANS( :lat )) * SIN( RADIANS( u.LATITUDE ))) as DISTANCE_IN_KMS, " +
//                    " ps.fk_pro_services_service_id,psc.STATUS,u.FIRST_NM,u.LAST_NM,pwd.AMOUNT,pd.* " +
//                    " FROM pro_details pd " +
//                    " JOIN pro_services ps ON pd.ID=ps.fk_pro_services_pro_id AND ps.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN users u ON pd.fk_pro_details_user_id=u.USER_ID AND u.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN pro_subscription psc ON pd.ID=psc.FK_PRO_ID AND psc.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN pro_wallet_details pwd ON pwd.FK_PRO_ID=pd.ID AND pwd.ACTIVE_STATUS=:activeStatus " +
//                    " HAVING " +
//                    " LOWER(IF(u.LAST_NM IS NULL, u.FIRST_NM, CONCAT(u.FIRST_NM, ' ', u.LAST_NM))) LIKE :term AND " +
//                    " ps.fk_pro_services_service_id IN (:serviceIds) AND " +
//                    " psc.STATUS = :subscriptionStatus AND " +
//                    " DISTANCE_IN_KMS < :serviceLocationRadius AND " +
//                    " pd.AVAILABILITY_STATUS IN (:availabilityStatus) AND " +
//                    " pwd.AMOUNT >= :thresholdValue"
//    )
    @Query(
            nativeQuery = true,
            value = "SELECT DISTINCT " +
                    " (ST_Distance_Sphere(point(:lng,:lat),point(u.LONGITUDE,u.LATITUDE)) * 0.001) as DISTANCE_IN_KMS, " +
                    " ps.fk_pro_services_service_id,psc.STATUS,u.FIRST_NM,u.LAST_NM,pwd.AMOUNT,pd.* " +
                    " FROM pro_details pd " +
                    " JOIN pro_services ps ON pd.ID=ps.fk_pro_services_pro_id AND ps.ACTIVE_STATUS=:activeStatus " +
                    " JOIN users u ON pd.fk_pro_details_user_id=u.USER_ID AND u.ACTIVE_STATUS=:activeStatus " +
                    " JOIN pro_subscription psc ON pd.ID=psc.FK_PRO_ID AND psc.ACTIVE_STATUS=:activeStatus " +
                    " JOIN pro_wallet_details pwd ON pwd.FK_PRO_ID=pd.ID AND pwd.ACTIVE_STATUS=:activeStatus " +
                    " HAVING " +
                    " LOWER(IF(u.LAST_NM IS NULL, u.FIRST_NM, CONCAT(u.FIRST_NM, ' ', u.LAST_NM))) LIKE :term AND " +
                    " ps.fk_pro_services_service_id IN (:serviceIds) AND " +
                    " psc.STATUS = :subscriptionStatus AND " +
                    " DISTANCE_IN_KMS < :serviceLocationRadius AND " +
                    " pd.AVAILABILITY_STATUS IN (:availabilityStatus) AND " +
                    " pwd.AMOUNT >= :thresholdValue"
    )
    List<ProDetails> fetchByNameAndProListingCriterias(@Param("term") String term, @Param("lat") double lat, @Param("lng") double lng, @Param("activeStatus") int activeStatus,
                                                       @Param("serviceIds") List<Long> serviceIds, @Param("subscriptionStatus") int subscriptionStatus,
                                                       @Param("serviceLocationRadius") float serviceLocationRadius, @Param("availabilityStatus") int availabilityStatus,
                                                       @Param("thresholdValue") float thresholdValue, Pageable pageable);

    //    @Query(
//            nativeQuery = true,
//            value = "SELECT COUNT(*) FROM (SELECT DISTINCT " +
//                    " ACOS( COS( RADIANS( :lat ) ) * COS( RADIANS( u.LATITUDE ) ) * COS( RADIANS( u.LONGITUDE ) - RADIANS( :lng ) ) + SIN( RADIANS( :lat )) * SIN( RADIANS( u.LATITUDE ))) as DISTANCE_IN_KMS, " +
//                    " ps.fk_pro_services_service_id,psc.STATUS,u.FIRST_NM,u.LAST_NM,pwd.AMOUNT,pd.* " +
//                    " FROM pro_details pd " +
//                    " JOIN pro_services ps ON pd.ID=ps.fk_pro_services_pro_id AND ps.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN users u ON pd.fk_pro_details_user_id=u.USER_ID AND u.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN pro_subscription psc ON pd.ID=psc.FK_PRO_ID AND psc.ACTIVE_STATUS=:activeStatus " +
//                    " JOIN pro_wallet_details pwd ON pwd.FK_PRO_ID=pd.ID AND pwd.ACTIVE_STATUS=:activeStatus " +
//                    " HAVING " +
//                    " LOWER(IF(u.LAST_NM IS NULL, u.FIRST_NM, CONCAT(u.FIRST_NM, ' ', u.LAST_NM))) LIKE :term AND " +
//                    " ps.fk_pro_services_service_id IN (:serviceIds) AND " +
//                    " psc.STATUS = :subscriptionStatus AND " +
//                    " DISTANCE_IN_KMS < :serviceLocationRadius AND " +
//                    " pd.AVAILABILITY_STATUS IN (:availabilityStatus) AND " +
//                    " pwd.AMOUNT >= :thresholdValue ) AS A"
//    )
    @Query(
            nativeQuery = true,
            value = "SELECT COUNT(*) FROM (SELECT DISTINCT " +
                    " (ST_Distance_Sphere(point(:lng,:lat),point(u.LONGITUDE,u.LATITUDE)) * 0.001) as DISTANCE_IN_KMS, " +
                    " ps.fk_pro_services_service_id,psc.STATUS,u.FIRST_NM,u.LAST_NM,pwd.AMOUNT,pd.* " +
                    " FROM pro_details pd " +
                    " JOIN pro_services ps ON pd.ID=ps.fk_pro_services_pro_id AND ps.ACTIVE_STATUS=:activeStatus " +
                    " JOIN users u ON pd.fk_pro_details_user_id=u.USER_ID AND u.ACTIVE_STATUS=:activeStatus " +
                    " JOIN pro_subscription psc ON pd.ID=psc.FK_PRO_ID AND psc.ACTIVE_STATUS=:activeStatus " +
                    " JOIN pro_wallet_details pwd ON pwd.FK_PRO_ID=pd.ID AND pwd.ACTIVE_STATUS=:activeStatus " +
                    " HAVING " +
                    " LOWER(IF(u.LAST_NM IS NULL, u.FIRST_NM, CONCAT(u.FIRST_NM, ' ', u.LAST_NM))) LIKE :term AND " +
                    " ps.fk_pro_services_service_id IN (:serviceIds) AND " +
                    " psc.STATUS = :subscriptionStatus AND " +
                    " DISTANCE_IN_KMS < :serviceLocationRadius AND " +
                    " pd.AVAILABILITY_STATUS IN (:availabilityStatus) AND " +
                    " pwd.AMOUNT >= :thresholdValue ) AS A"
    )
    Integer countByNameAndProListingCriterias(@Param("term") String term, @Param("lat") double lat, @Param("lng") double lng, @Param("activeStatus") int activeStatus,
                                              @Param("serviceIds") List<Long> serviceIds, @Param("subscriptionStatus") int subscriptionStatus,
                                              @Param("serviceLocationRadius") float serviceLocationRadius, @Param("availabilityStatus") int availabilityStatus,
                                              @Param("thresholdValue") float thresholdValue);

    @Query("select distinct pd.userDetails from ProDetails pd where pd.id IN (?1)")
    Users fetchByUserIdIn(Long proId);

    ProDetails findByUserDetails_UserId(String userId);

    @Query("SELECT MAX(pd.applicationNo) from ProDetails pd")
    int fetchMaxApplicationNo();


}
