package com.prajekpro.api.repository;

import com.prajekpro.api.domain.AppointmentDetails;
import com.prajekpro.api.enums.AppointmentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentDetailsRepository extends JpaRepository<AppointmentDetails, Long>, JpaSpecificationExecutor<AppointmentDetails> {

    @Query(nativeQuery = true, value = "select * from appointment_details ad inner join appointment_requested_services ars" +
            " on ad.ID = ars.FK_MASTER_ID where ad.BOOKED_BY = :userId" +
            " AND ars.APPOINTMENT_DATE >= :currentDate AND ad.state IN (:state)" +
            " order by ars.APPOINTMENT_DATE ASC, ars.APPOINTMENT_TIME ASC")
    List<AppointmentDetails> findByBookedByAndStateCurrentAppointment(@Param("userId") String userId, @Param("state") List<Integer> state, @Param("currentDate") LocalDate currentDate, Pageable pageRequest);

    @Query(
            nativeQuery = true,
            value = "select * from appointment_details ad inner join appointment_requested_services ars" +
                    " on ad.ID = ars.FK_MASTER_ID where ad.BOOKED_BY = :userId" +
                    " AND ad.state IN (:state)" +
                    " order by ars.APPOINTMENT_DATE DESC"
    )
    List<AppointmentDetails> findByBookedByAndStatePastAppointment(@Param("userId") String userId, @Param("state") List<Integer> state, Pageable pageRequest);


    @Query(nativeQuery = true, value = "select (arssc.REQUESTED_PRICE * arssc.REQUESTED_QTY) from appointment_details ad " +
            " inner join appointment_requested_services ars on ad.ID=ars.FK_MASTER_ID" +
            " inner join appointment_requested_service_categories arsc on ars.ID=arsc.MASTER_ID" +
            " inner join appointment_requested_service_sub_categories arssc on arsc.ID=arssc.MASTER_ID" +
            " where ad.BOOKED_FOR= :proId AND ad.STATE= :state AND ars.APPOINTMENT_DATE >= CAST(CURRENT_TIMESTAMP AS DATE)")
    List<Float> findByBookedForAndState(@Param("proId") Long proId, @Param("state") Integer state);

    @Query(
            nativeQuery = true, value = "select * from appointment_details ad " +
            " inner join appointment_requested_services ars on ad.ID = ars.FK_MASTER_ID " +
            " where ad.BOOKED_FOR = :bookedFor" +
            " AND ars.APPOINTMENT_DATE >= :currDate AND ars.FK_SERVICE_ID = :serviceId" +
            " AND ad.state IN (:state)" +
            " order by ars.APPOINTMENT_DATE ASC, ars.APPOINTMENT_TIME ASC"
    )
    Page<AppointmentDetails> findByBookedForAndServiceId(@Param("bookedFor") Long bookedFor, @Param("state") List<Integer> state, @Param("serviceId") Long serviceId, @Param("currDate") LocalDate currDate, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "select * from appointment_details ad " +
                    " inner join appointment_requested_services ars on ad.ID=ars.FK_MASTER_ID " +
                    " inner join appointment_requested_service_categories arsc on arsc.MASTER_ID=ars.ID" +
                    " inner join service_item_category sic on sic.ID=arsc.SERVICE_CATEGORY_ID" +
                    " inner join appointment_requested_service_sub_categories arssc on arssc.MASTER_ID=arsc.ID" +
                    " inner join service_item_subcategory sisc on sisc.ID=arssc.SERVICE_SUB_CATEGORY_ID" +
                    " inner join users u on u.USER_ID=ad.BOOKED_BY " +
                    " LEFT JOIN user_delivery_address uda on ad.FK_USER_DELIVERY_ADDRESS_ID=uda.ID " +
                    " where ad.BOOKED_FOR = :bookedFor and ad.state IN (:state) and ars.FK_SERVICE_ID = :serviceId" +
                    " and ars.APPOINTMENT_DATE >= :currDate " +
                    " and (LOWER(u.FIRST_NM) LIKE :term OR LOWER(u.LAST_NM) LIKE :term OR LOWER(uda.ADDRESS_LINE) LIKE :term " +
                    " OR LOWER(uda.HOUSE_FLAT_SHOP_BLOCK_NO) LIKE :term OR LOWER(uda.LANDMARK) LIKE :term " +
                    " OR LOWER(sic.display_value) LIKE :term OR LOWER(sisc.service_item_sub_category_name) LIKE :term)" +
                    " order by ars.APPOINTMENT_DATE ASC, ars.APPOINTMENT_TIME ASC"
    )
    Page<AppointmentDetails> findByBookedForAndServiceIdAndSearchTerm(
            @Param("bookedFor") Long bookedFor,
            @Param("state") List<Integer> state,
            @Param("serviceId") Long serviceId,
            @Param("currDate") LocalDate currDate,
            @Param("term") String term,
            Pageable pageable);


    @Query(
            nativeQuery = true,
            value = "select * from appointment_details ad inner join appointment_requested_services ars" +
                    " on ad.ID = ars.FK_MASTER_ID where ad.BOOKED_FOR = :bookedFor " +
                    " AND ad.state IN (:state) AND ars.FK_SERVICE_ID = :serviceId " +
                    " AND ars.APPOINTMENT_DATE >= :currentDate " +
                    " order by ars.APPOINTMENT_DATE ASC, ars.APPOINTMENT_TIME ASC")
    Page<AppointmentDetails> findByBookedFor_IdAndStateConfirmed(
            @Param("bookedFor") Long proId,
            @Param("state") List<Integer> state,
            @Param("currentDate") LocalDate currentDate,
            @Param("serviceId") Long serviceId,
            Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "select * from appointment_details ad " +
                    " inner join appointment_requested_services ars on ad.ID = ars.FK_MASTER_ID " +
                    " inner join users u on u.USER_ID=ad.BOOKED_BY " +
                    " LEFT JOIN user_delivery_address uda on ad.FK_USER_DELIVERY_ADDRESS_ID=uda.ID " +
                    " where ad.BOOKED_FOR = :bookedFor AND ad.state IN (:state) AND ars.FK_SERVICE_ID = :serviceId " +
                    " AND ars.APPOINTMENT_DATE >= :currentDate " +
                    " and (LOWER(u.FIRST_NM) LIKE :term OR LOWER(u.LAST_NM) LIKE :term OR LOWER(uda.ADDRESS_LINE) LIKE :term)" +
                    " order by ars.APPOINTMENT_DATE ASC, ars.APPOINTMENT_TIME ASC")
    Page<AppointmentDetails> findByBookedFor_IdAndStateConfirmedAndSearchTerm(
            @Param("bookedFor") Long proId,
            @Param("state") List<Integer> state,
            @Param("currentDate") LocalDate currentDate,
            @Param("serviceId") Long serviceId,
            @Param("term") String term,
            Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "select * from appointment_details ad inner join appointment_requested_services ars" +
                    " on ad.ID = ars.FK_MASTER_ID where ad.BOOKED_FOR = :bookedFor " +
                    " AND ad.state IN (:state) AND ars.FK_SERVICE_ID = :serviceId " +
                    " order by ars.APPOINTMENT_DATE DESC, ars.APPOINTMENT_TIME DESC")
    Page<AppointmentDetails> findByBookedFor_IdAndStatePast(@Param("bookedFor") Long proId, @Param("state") List<Integer> pastState, @Param("serviceId") Long serviceId, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "select * from appointment_details ad " +
                    " inner join appointment_requested_services ars on ad.ID=ars.FK_MASTER_ID " +
                    " inner join appointment_requested_service_categories arsc on arsc.MASTER_ID=ars.ID" +
                    " inner join service_item_category sic on sic.ID=arsc.SERVICE_CATEGORY_ID" +
                    " inner join appointment_requested_service_sub_categories arssc on arssc.MASTER_ID=arsc.ID" +
                    " inner join service_item_subcategory sisc on sisc.ID=arssc.SERVICE_SUB_CATEGORY_ID" +
                    " inner join users u on u.USER_ID=ad.BOOKED_BY " +
                    " LEFT JOIN user_delivery_address uda on ad.FK_USER_DELIVERY_ADDRESS_ID=uda.ID " +
                    " where ad.BOOKED_FOR = :bookedFor and ad.state IN (:state) and ars.FK_SERVICE_ID = :serviceId" +
                    " and (LOWER(u.FIRST_NM) LIKE :term OR LOWER(u.LAST_NM) LIKE :term OR LOWER(uda.ADDRESS_LINE) LIKE :term " +
                    " OR LOWER(uda.HOUSE_FLAT_SHOP_BLOCK_NO) LIKE :term OR LOWER(uda.LANDMARK) LIKE :term " +
                    " OR LOWER(sic.display_value) LIKE :term OR LOWER(sisc.service_item_sub_category_name) LIKE :term)" +
                    " order by ars.APPOINTMENT_DATE DESC, ars.APPOINTMENT_TIME DESC	")
    Page<AppointmentDetails> findByBookedFor_IdAndStatePastAndSearchTerm(
            @Param("bookedFor") Long proId,
            @Param("state") List<Integer> pastState,
            @Param("serviceId") Long serviceId,
            @Param("term") String term,
            Pageable pageable);

    @Query("select count(ad) from AppointmentDetails ad where ad.bookedFor.id = :proId AND " +
            " ad.state = :state ")
    Long countOfCompletedAppointment(@Param("proId") Long proId, @Param("state") AppointmentState state);

    List<AppointmentDetails> findByBookedFor_Id(Long proId);

    @Query("select count(ad) from AppointmentDetails ad where " +
            " ad.state = :state ")
    Long countOfAppointmentsByState(@Param("state") AppointmentState state);

    @Query(nativeQuery = true, value = "select count(*) from appointment_details ad inner join appointment_requested_services ars " +
            " on ad.ID = ars.FK_MASTER_ID where " +
            " ars.APPOINTMENT_DATE = CAST(CURRENT_TIMESTAMP AS DATE)")
    Long countOfCurrentDateAppointments();

    @Query(nativeQuery = true, value = "select count(*) from appointment_details ad inner join appointment_requested_services ars " +
            " on ad.ID = ars.FK_MASTER_ID where " +
            " ars.APPOINTMENT_DATE = CAST(CURRENT_TIMESTAMP AS DATE) AND ad.STATE = :state")
    Long countOfCurrentDateAppointmentsByState(@Param("state") Integer state);


    @Query("select count(ad) from AppointmentDetails ad where ad.bookedFor.id = :proId ")
    int countOfTotalAppointmentsByProId(@Param("proId") Long proId);

    @Query(nativeQuery = true, value = "select count(*) from appointment_details ad inner join appointment_requested_services ars " +
            " on ad.ID = ars.FK_MASTER_ID where ad.BOOKED_FOR = :proId AND" +
            " ars.APPOINTMENT_DATE = CAST(CURRENT_TIMESTAMP AS DATE)")
    int countOfTodaysAppointmentsByProId(@Param("proId") Long proId);

    List<AppointmentDetails> findByBookedFor_IdAndStateIn(Long proId, List<AppointmentState> state, Pageable pageable);

    Page<AppointmentDetails> findByBookedBy_UserIdAndStateIn(String userId, List<AppointmentState> state, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from appointment_details ad inner join appointment_requested_services ars" +
            "  on ad.ID = ars.FK_MASTER_ID where ad.BOOKED_BY = :userId" +
            "  AND ars.APPOINTMENT_DATE >= CAST(CURRENT_TIMESTAMP AS DATE) AND ad.state IN (:state) " +
            "  AND ars.APPOINTMENT_TIME >= CAST(CURRENT_TIMESTAMP AS TIME(0))" +
            "  order by ars.APPOINTMENT_DATE ASC, ars.APPOINTMENT_TIME ASC")
    Page<AppointmentDetails> findByBookedByAndState(@Param("userId") String userId, @Param("state") List<Integer> state, Pageable pageable);


    @Query("select count(ad) from AppointmentDetails ad where ad.bookedBy.userId = :userId and ad.state IN (:state) ")
    int countOfAppointmentsByBookedBy(@Param("userId") String userId, @Param("state") List<AppointmentState> state);

    @Query(nativeQuery = true, value = "select count(*) from appointment_details ad " +
            " inner join appointment_requested_services ars" +
            " on ad.ID=ars.FK_MASTER_ID" +
            "  where ad.BOOKED_FOR = :proId and ad.state IN (:state) and ars.FK_SERVICE_ID = :serviceId" +
            "  and ars.APPOINTMENT_DATE >= :currentDate")
    Long countOfProAppointmentsByServiceAndState(@Param("proId") Long proId, @Param("state") List<Integer> state, @Param("currentDate") LocalDate currentDate, @Param("serviceId") Long serviceId);

    @Query(
            nativeQuery = true,
            value = "select count(*) from appointment_details ad " +
                    " inner join appointment_requested_services ars on ad.ID=ars.FK_MASTER_ID" +
                    " inner join users u on u.USER_ID=ad.BOOKED_BY " +
                    " LEFT JOIN user_delivery_address uda on ad.FK_USER_DELIVERY_ADDRESS_ID=uda.ID " +
                    "  where ad.BOOKED_FOR = :proId and ad.state IN (:state) and ars.FK_SERVICE_ID = :serviceId" +
                    "  and ars.APPOINTMENT_DATE >= :currentDate " +
                    " and (LOWER(u.FIRST_NM) LIKE :term OR LOWER(u.LAST_NM) LIKE :term OR LOWER(uda.ADDRESS_LINE) LIKE :term)"
    )
    Long countOfProAppointmentsByServiceAndStateAndSearchTerm(@Param("proId") Long proId,
                                                              @Param("state") List<Integer> state,
                                                              @Param("currentDate") LocalDate currentDate,
                                                              @Param("serviceId") Long serviceId,
                                                              @Param("term") String term);

    @Query(nativeQuery = true, value = "select count(*) from appointment_details ad " +
            " inner join appointment_requested_services ars" +
            " on ad.ID=ars.FK_MASTER_ID" +
            "  where ad.BOOKED_FOR = :proId and ad.state IN (:state) and ars.FK_SERVICE_ID = :serviceId")
    Long countOfProAppByServiceAndState(@Param("proId") Long proId, @Param("state") List<Integer> state, @Param("serviceId") Long serviceId);

    @Query(
            nativeQuery = true,
            value = "SELECT distinct ad.* FROM appointment_requested_services ars \n" +
                    "inner join appointment_details ad on ars.FK_MASTER_ID=ad.ID \n" +
                    "where APPOINTMENT_DATE <= :apptDate and ad.STATE IN (:apptStatesToAutoCancel) and ad.ACTIVE_STATUS=:activeStatus"
    )
    List<AppointmentDetails> selectApptForAutoCancellation(@Param("apptStatesToAutoCancel") List<Integer> apptStatesToAutoCancel,
                                                           @Param("apptDate") String apptDate, @Param("activeStatus") int activeStatus);

    /**
     * Interface ProJobsCount to store and share jobs completed by PRO
     */
    public static interface ProJobsCompleted {
        Long getProId();
        Integer getJobsCompleted();
    }


    /**
     *
     * @param proIds
     * @param apptStates
     * @param activeStatus
     * @return
     */
    @Query(
            nativeQuery = true,
            value = "SELECT BOOKED_FOR AS proId, COUNT(*) AS jobsCompleted FROM appointment_details" +
                    " WHERE BOOKED_FOR IN (:proIds) AND STATE IN (:apptState) AND ACTIVE_STATUS IN (:activeStatus) GROUP BY proId"
    )
    List<ProJobsCompleted> countJobsCompletedForProIdIn(@Param("proIds") List<Long> proIds,
                                                          @Param("apptState") List<Integer> apptStates,
                                                          @Param("activeStatus") List<Integer> activeStatus);
}
