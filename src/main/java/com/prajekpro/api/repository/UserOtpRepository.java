package com.prajekpro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prajekpro.api.domain.UserOtp;
import com.safalyatech.common.repository.IBaseRepository;

@Repository
public interface UserOtpRepository extends JpaRepository<UserOtp, Long>, IBaseRepository<UserOtp> {

	UserOtp findByContactNo(String contactNo);

	@Query("SELECT uo FROM UserOtp uo where uo.contactNo= :contactNo and uo.user.userId= :userId ")
    UserOtp findByContactNoAndUserId(@Param("contactNo") String contactNo, @Param("userId") String userId);
}
