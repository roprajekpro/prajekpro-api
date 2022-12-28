package com.prajekpro.api.service;

import com.prajekpro.api.dto.PushNotificationRequest;
import com.prajekpro.api.dto.UserNotificationTokenDTO;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;
import java.util.Set;

public interface PushNotificationService {

    @Async
    void sendPushNotification(Map<String, String> data, PushNotificationRequest request);

    @Async
    void sendPushNotificationToMultipleToken(Map<String, String> payloadData, PushNotificationRequest pushNotificationRequest);

    void sendPushNotificationWithoutData(PushNotificationRequest request);

    void sendPushNotificationToToken(PushNotificationRequest request);

    BaseWrapper sendNotificationToken(UserNotificationTokenDTO request) throws ServicesException;

    Map<String, String> getPayloadData();

    Map<String, String> getPayloadDataForChat();

    BaseWrapper getNotificationList(Pageable pageable);

    BaseWrapper getClearedAllNotification();

    BaseWrapper getClearNotificationById(Set<Long> notificationId) throws ServicesException;
}
