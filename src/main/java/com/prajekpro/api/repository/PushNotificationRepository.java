package com.prajekpro.api.repository;


import com.prajekpro.api.domain.UserNotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PushNotificationRepository extends JpaRepository<UserNotificationToken, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO user_notification_tokens(USER_ID, DEVICE_ID, TOKEN, CREATED_BY, MODIFIED_BY, CREATED_TS, MODIFIED_TS)   \n" +
            "VALUES (:userId, :deviceId, :token, :createdBy, :modifiedBy, :createdTs, :modifiedTs )  \n" +
            "ON DUPLICATE KEY UPDATE  USER_ID = :userId,  DEVICE_ID = :deviceId, TOKEN = :token,CREATED_BY = :createdBy, MODIFIED_BY = :modifiedBy, CREATED_TS = :createdTs, MODIFIED_TS = :modifiedTs")
    void replaceTokenByUserIdAndDeviceId(@Param("userId") String userId, @Param("deviceId") String deviceId, @Param("token") String token,
                                         @Param("createdBy") String createdBy, @Param("modifiedBy") String modifiedBy, @Param("createdTs") Long createdTs,
                                         @Param("modifiedTs") Long modifiedTs);

    @Query(nativeQuery = true, value = "Select unt.TOKEN from user_notification_tokens unt where unt.USER_ID = :userId")
    List<String> findTokenListByUserId(@Param("userId") String fetchLoggedInUser);

    void deleteUserNotificationTokenByUserIdAndDeviceId(String fetchLoggedInUser, String deviceId);
}
