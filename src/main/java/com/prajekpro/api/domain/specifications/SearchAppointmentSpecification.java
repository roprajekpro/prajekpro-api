package com.prajekpro.api.domain.specifications;

import com.prajekpro.api.domain.AppointmentDetails;
import com.prajekpro.api.domain.AppointmentRequestedServices;
import com.prajekpro.api.domain.Services;
import com.prajekpro.api.dto.AppointmentSearchRequestBodyDTO;
import com.prajekpro.api.dto.LatLongVO;
import com.prajekpro.api.dto.ServicesDTO;
import com.prajekpro.api.enums.AppointmentState;
import com.safalyatech.common.utility.CheckUtil;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
public class SearchAppointmentSpecification implements Specification<AppointmentDetails> {

    private AppointmentSearchRequestBodyDTO request;

    public SearchAppointmentSpecification(AppointmentSearchRequestBodyDTO request) {
        this.request = request;
    }

    @Override
    public Predicate toPredicate(Root<AppointmentDetails> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicates = new ArrayList<>();

        //Add Predicate for appointment requested service for start date, end date and services criteria passed in request
        prepareAppointmentRequestedServicePredicate(request, criteriaBuilder, root, predicates);

        // Add predicate for Latitude and Longitude
        if (CheckUtil.hasValue(request.getLatLong())) {
            List<LatLongVO> latLongList = request.getLatLong();
            int latLongSize = latLongList.size();
            Predicate latLongArray[] = new Predicate[latLongSize];

            for (int i = 0; i < latLongSize; i++)
                latLongArray[i] = prepareLatitudeAndLongitudePredicate(latLongList.get(i), criteriaBuilder, root);

            Predicate predicate = criteriaBuilder.or(latLongArray);
            predicates.add(predicate);
        }

        // add predicate for Appointment state
        if (CheckUtil.hasValue(request.getState())) {
            Predicate predicate = prepareAppointmentStatePredicate(request.getState(), criteriaBuilder, root);
            predicates.add(predicate);
        }

        //Add predicate for proId
        if (CheckUtil.hasValue(request.getProId())) {
            Predicate predicate = prepareProIdPredicate(request.getProId(), criteriaBuilder, root);
            predicates.add(predicate);
        }

        //Add predicate for customerId
        if (CheckUtil.hasValue(request.getCustomerId())) {
            Predicate predicate = prepareCustomerIdPredicate(request.getCustomerId(), criteriaBuilder, root);
            predicates.add(predicate);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private Predicate prepareCustomerIdPredicate(List<String> customerIdList, CriteriaBuilder criteriaBuilder, Root<AppointmentDetails> root) {

        return criteriaBuilder.and(root.get("bookedBy")
                .<Long>get("userId").in(customerIdList));
    }

    private Predicate prepareProIdPredicate(List<Long> proIdList, CriteriaBuilder criteriaBuilder, Root<AppointmentDetails> root) {
        return criteriaBuilder.and(root.get("bookedFor")
                .<Long>get("id").in(proIdList));
    }

    private Predicate prepareAppointmentStatePredicate(List<AppointmentState> stateList, CriteriaBuilder criteriaBuilder, Root<AppointmentDetails> root) {
        return criteriaBuilder.and(root.get("state").in(stateList));
    }

    private Predicate prepareLatitudeAndLongitudePredicate(LatLongVO latLong, CriteriaBuilder
            criteriaBuilder, Root<AppointmentDetails> root) {
        return criteriaBuilder.and(
                criteriaBuilder.equal(
                        root.get("userDeliveryAddress").<Double>get("latitude"), latLong.getLatitude()),
                criteriaBuilder.equal(
                        root.get("userDeliveryAddress").<Double>get("longitude"), latLong.getLongitude())
        );
    }

    /**
     * @param request
     * @param criteriaBuilder
     * @param root
     * @param predicateList
     */
    private void prepareAppointmentRequestedServicePredicate(AppointmentSearchRequestBodyDTO request, CriteriaBuilder criteriaBuilder,
                                                             Root<AppointmentDetails> root, List<Predicate> predicateList) {

        Join<AppointmentDetails, AppointmentRequestedServices> requestedServicesJoin = root.join("appointmentRequestedServices");

        //Add start date criteria
        if (CheckUtil.hasValue(request.getStartDate())) {
            predicateList.add(
                    criteriaBuilder.greaterThanOrEqualTo(requestedServicesJoin.get("appointmentDate"), request.getStartDate()));
        }

        //Add end date criteria
        if (CheckUtil.hasValue(request.getEndDate())) {
            predicateList.add(
                    criteriaBuilder.lessThanOrEqualTo(requestedServicesJoin.get("appointmentDate"), request.getEndDate()));
        }

        //Add services criteria
        if (CheckUtil.hasValue(request.getServices())) {
            List<Long> serviceIDs = request.getServices().stream().map(ServicesDTO::getId).collect(Collectors.toList());

            Join<AppointmentRequestedServices, Services> appointmentServicesJoin = requestedServicesJoin.join("services");
            predicateList.add(
                    criteriaBuilder.in(appointmentServicesJoin.get("id")).value(serviceIDs));
        }
    }

}
