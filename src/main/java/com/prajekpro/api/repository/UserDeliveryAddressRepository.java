package com.prajekpro.api.repository;

import com.prajekpro.api.domain.UserDeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDeliveryAddressRepository extends JpaRepository<UserDeliveryAddress,Long> {

    List<UserDeliveryAddress> findByUsers_UserIdAndActiveStatus(String userId, int i);
}
