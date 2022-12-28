package com.prajekpro.api.dto;

import com.prajekpro.api.domain.UserNotification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserNotificationDTO {

    private Long notificationId;
    private String userId;
    private String title;
    private String metaData;
    private boolean isRead;
    private boolean isCleared;
    private Long notificationTime;

    public UserNotificationDTO(UserNotification notification) {

        this.notificationId = notification.getId();
        this.userId = notification.getUsers().getUserId();
        this.title = notification.getTitle();
        this.metaData = notification.getMetaData();
        this.isRead = notification.isRead();
        this.isCleared = notification.isCleared();
        this.notificationTime = notification.getModifiedTs();

    }
}
