package com.prajekpro.api.repository;

import com.prajekpro.api.domain.ProWalletDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletDetailsRepository extends JpaRepository<ProWalletDetails,Long> {
    ProWalletDetails findByProDetails_IdAndActiveStatus(Long id, int activeStatus);

    ProWalletDetails findByProDetails_UserDetails_UserIdAndActiveStatus(String userId, int activeStatus);
}
