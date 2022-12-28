package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.dto.PushNotificationRequest;
import com.prajekpro.api.dto.PushNotificationResponse;
import com.prajekpro.api.dto.UserNotificationTokenDTO;
import com.prajekpro.api.service.PushNotificationService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class PushNotificationController {

    @Autowired
    private PushNotificationService pushNotificationService;

    @PostMapping("/notification/topic")
    public ResponseEntity sendNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationWithoutData(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/token")
    public ResponseEntity sendTokenNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationToToken(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/data")
    public ResponseEntity sendDataNotification(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotification(pushNotificationService.getPayloadData(), request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/data/multiple")
    public ResponseEntity sendDataNotificationToMultipleToken(@RequestBody PushNotificationRequest request) {
        pushNotificationService.sendPushNotificationToMultipleToken(pushNotificationService.getPayloadDataForChat(), request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/notification/device_token")
    public BaseWrapper sendDataNotification(@RequestBody UserNotificationTokenDTO request) throws ServicesException {
        return pushNotificationService.sendNotificationToken(request);
    }

    @ApiOperation(value = "api to get of List notifications")
    @GetMapping(value = {RestUrlConstants.PP_NOTIFICATIONS})
    public BaseWrapper getNotificationList(Pageable pageable) {
        return pushNotificationService.getNotificationList(pageable);
    }

    @ApiOperation(value = "api to clear all notifications")
    @PutMapping(value = {RestUrlConstants.PP_CLEARALL_NOTIFICATION})
    public BaseWrapper getClearedAllNotifications() {
        return pushNotificationService.getClearedAllNotification();
    }

    @ApiOperation(value = "api to clear notifications")
    @PutMapping(value = {RestUrlConstants.PP_CLEAR_NOTIFICATION})
    public BaseWrapper getClearedNotificationById(@RequestBody Set<Long> notificationId) throws ServicesException {
        return pushNotificationService.getClearNotificationById(notificationId);
    }


}
