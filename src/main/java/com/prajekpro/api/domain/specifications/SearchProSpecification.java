package com.prajekpro.api.domain.specifications;

import com.prajekpro.api.domain.ProDetails;
import com.prajekpro.api.domain.ProSubscription;
import com.prajekpro.api.dto.LatLongVO;
import com.prajekpro.api.dto.ProSearchRequestBodyDTO;
import com.safalyatech.common.utility.CheckUtil;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class SearchProSpecification implements Specification<ProDetails> {

    private ProSearchRequestBodyDTO request;

    public SearchProSpecification(ProSearchRequestBodyDTO request) {
        this.request = request;
    }

    @Override
    public Predicate toPredicate(Root<ProDetails> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

//add predicated for Active status
        if (CheckUtil.hasValue(request.getActiveStatus())) {
            Predicate predicate = prepareActiveStatusPredicate(request.getActiveStatus(), criteriaBuilder, root);
            predicates.add(predicate);
        }

        //Add Predicate for ratings
        if (CheckUtil.hasValue(request.getRatings())) {
            Predicate predicate = prepareRatingsPredicate(request.getRatings(), criteriaBuilder, root);
            predicates.add(predicate);
        }

        // Add predicate for Latitude and Longitude
        if (CheckUtil.hasValue(request.getLatLongList())) {
            List<LatLongVO> latLongList = request.getLatLongList();
            int latLongSize = latLongList.size();
            Predicate latLongArray[] = new Predicate[latLongSize];

            for (int i = 0; i < latLongSize; i++)
                latLongArray[i] = prepareLatitudeAndLongitudePredicate(latLongList.get(i), criteriaBuilder, root);

            Predicate predicate = criteriaBuilder.or(latLongArray);
            predicates.add(predicate);
        }
        //TODO:predicates for subscription left
        prepareProSubscriptionPredicates(request, criteriaBuilder, root, predicates);

        query.groupBy(root.get("id"));
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private Predicate prepareActiveStatusPredicate(List<Integer> activeStatus, CriteriaBuilder criteriaBuilder, Root<ProDetails> root) {
        return criteriaBuilder.and(root.get("userDetails").get("activeStatus").in(activeStatus));
    }

    private void prepareProSubscriptionPredicates(ProSearchRequestBodyDTO request, CriteriaBuilder criteriaBuilder, Root<ProDetails> root, List<Predicate> predicatesList) {
        Join<ProDetails, ProSubscription> proSubscriptionJoin = root.join("proSubscriptions", JoinType.LEFT);
        // Join<SubscriptionDetails, ProSubscription> proSubscriptionJoin = subscriptionDetailsJoin.join("proSubscriptionList",JoinType.LEFT);

        //add predicates for subscription status
        if (CheckUtil.hasValue(request.getSubscriptionStatus())) {
            predicatesList.add(
                    criteriaBuilder.and(proSubscriptionJoin.get("subscriptionStatus").in(request.getSubscriptionStatus())));
        }

        //add predicates for subscription date from
        if (CheckUtil.hasValue(request.getSubscriptionDateFrom())) {
            predicatesList.add(
                    criteriaBuilder.greaterThanOrEqualTo(proSubscriptionJoin.get("dateOfSubscription"), request.getSubscriptionDateFrom()));
        }

        //add predicates for subscription date till
        if (CheckUtil.hasValue(request.getSubscriptionDateTill())) {
            predicatesList.add(
                    criteriaBuilder.lessThanOrEqualTo(proSubscriptionJoin.get("subscriptionExpiresOn"), request.getSubscriptionDateTill()));
        }
    }

    private Predicate prepareLatitudeAndLongitudePredicate(LatLongVO latLongVO, CriteriaBuilder criteriaBuilder, Root<ProDetails> root) {
        return criteriaBuilder.and(
                criteriaBuilder.equal(
                        root.get("userDetails").<Double>get("locLatitude"), latLongVO.getLatitude()),
                criteriaBuilder.equal(
                        root.get("userDeliveryAddress").<Double>get("locLongitude"), latLongVO.getLongitude())
        );
    }

    private Predicate prepareRatingsPredicate(List<Long> ratings, CriteriaBuilder criteriaBuilder, Root<ProDetails> root) {
        return criteriaBuilder.and(root.get("ratings").in(ratings));
    }

}
