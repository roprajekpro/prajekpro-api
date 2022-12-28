package com.prajekpro.api.repository;

import com.prajekpro.api.domain.MasterSubscription;
import com.prajekpro.api.enums.SubscriptionTypes;
import com.safalyatech.common.repository.IBaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterSubscriptionRepository extends JpaRepository<MasterSubscription, Long>, IBaseRepository<MasterSubscription> {

    MasterSubscription findBySubscriptionTypeAndActiveStatus(SubscriptionTypes renewSubscription, Integer activeStatus);

    List<MasterSubscription> findAllBySubscriptionTypeAndActiveStatus(SubscriptionTypes subscriptionType, Integer activeStatus);
}
