package com.prajekpro.api.repository;

import com.prajekpro.api.domain.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification,Long> {

    Page<UserNotification> findByUsers_UserIdAndIsCleared(String userId, boolean i, Pageable pageable);

    @Modifying
    @Query("UPDATE UserNotification SET isCleared = 1 WHERE users.userId = ?1 ")
    int updateIsClear(String userId);

    @Modifying
    @Query("UPDATE UserNotification SET isCleared = 1 WHERE id IN (?1) ")
    int getClearNotificationById(Set<Long> notificationId);

    @Modifying
    @Query("UPDATE UserNotification SET isRead = 1 WHERE id IN (?1) ")
    int updateIsRead(Set<Long> unreadMessageIds);
}
