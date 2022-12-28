package com.prajekpro.api.dto;

import com.prajekpro.api.enums.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PushNotificationRequest {

    private String title;
    private String message;
    private String topic;
    private String token;
    private List<String> tokenList;
    private NotificationType notificationType;
    private String notificationTime;

    public PushNotificationRequest(String title, String message, String token) {
        this.title = title;
        this.message = message;
        this.token = token;
    }

    public PushNotificationRequest(String title, String message, List<String> tokenList, NotificationType notificationType) {
        this.title = title;
        this.message = message;
        this.tokenList = tokenList;
        this.notificationType = notificationType;
        this.notificationTime = String.valueOf(System.currentTimeMillis());
    }

}