package com.prajekpro.api.service.impl;


import com.google.firebase.messaging.*;
import com.google.gson.*;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import javax.transaction.*;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class PushNotificationServiceImpl implements PushNotificationService {

    @Autowired
    private PushNotificationRepository pushNotificationRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired private UserNotificationRepository userNotificationRepository;

    @Async
    @Override
    public void sendPushNotification(Map<String, String> data, PushNotificationRequest request) {
        try {
            Message message = getPreconfiguredMessageWithData(data, request);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(message);
            log.debug("Notification : "," msg " + jsonOutput);
            String response = sendAndGetResponse(message);
            log.debug("Notification : ", "Sent message with data." + ", " + response + " msg " + jsonOutput);
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public BaseWrapper sendNotificationToken(UserNotificationTokenDTO request) throws ServicesException {

        if (!CheckUtil.hasValue(request.getToken()) || !CheckUtil.hasValue(request.getDeviceId()))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        UserNotificationToken userNotificationToken = new UserNotificationToken(authorizationService.fetchLoggedInUser().getUserId(), request.getDeviceId(), request.getToken());
        userNotificationToken.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        log.debug("userNotificationToken: {}", userNotificationToken.toString());
        pushNotificationRepository.replaceTokenByUserIdAndDeviceId(userNotificationToken.getUserId(), userNotificationToken.getDeviceId(), userNotificationToken.getToken(),
                userNotificationToken.getCreatedBy(), userNotificationToken.getModifiedBy(), userNotificationToken.getCreatedTs(), userNotificationToken.getModifiedTs());

        return new BaseWrapper();
    }


    @Async
    @Override
    public void sendPushNotificationToMultipleToken(Map<String, String> payloadData,PushNotificationRequest pushNotificationRequest) {
        final String METHOD_NM = "sendPushNotificationToMultipleToken";
        final String CLASS_NM = getClass().getName();

        payloadData.put("notificationType", String.valueOf(pushNotificationRequest.getNotificationType()));
        payloadData.put("notificationTime", String.valueOf(pushNotificationRequest.getNotificationTime()));

        String pushNotificationMessage =pushNotificationRequest.getMessage().replace("<br>"," ");
        String pushNotificationMessageUpdated =pushNotificationMessage.replace("<br />"," ");
        MulticastMessage message = MulticastMessage.builder().putAllData(payloadData)
                .addAllTokens(pushNotificationRequest.getTokenList())
                .setNotification(new Notification(pushNotificationRequest.getTitle(), pushNotificationMessageUpdated))
                .build();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        log.debug("Notification : "," To Multiple tokens " + jsonOutput);
        try {
            BatchResponse response = sendAndGetMulticastMessage(message);
            log.debug("Notification : ", "Multicast :"+response.toString());
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        // The order of responses corresponds to the order of the registration tokens.
                        failedTokens.add(pushNotificationRequest.getTokenList().get(i));
                    }
                }

                log.error("List of tokens that caused failures: " + failedTokens);
            }

        } catch (InterruptedException e) {
            log.error(GlobalConstants.LOG.ERROR, METHOD_NM, CLASS_NM, e);
        } catch (ExecutionException e) {
            log.error(GlobalConstants.LOG.ERROR, METHOD_NM, CLASS_NM, e);
        } catch (FirebaseMessagingException e) {
            log.error(GlobalConstants.LOG.ERROR, METHOD_NM, CLASS_NM, e);
        }
    }


    private Message getPreconfiguredMessageWithData(Map<String, String> data, PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).putAllData(data).setToken(request.getToken())
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        return Message.builder()
                /*.setApnsConfig(apnsConfig).setAndroidConfig(androidConfig)*/.setNotification(
                        new Notification(request.getTitle(), request.getMessage()));
    }

    private AndroidConfig getAndroidConfig(String bodyJson) {
        return AndroidConfig.builder()
                .setNotification(AndroidNotification.builder().setBody(bodyJson).build()).build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder().putCustomData("message","" /*Chat Object*/)
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private BatchResponse sendAndGetMulticastMessage(MulticastMessage message) throws InterruptedException, ExecutionException, FirebaseMessagingException {
        return FirebaseMessaging.getInstance().sendMulticast(message);
    }

    @Override
    public void sendPushNotificationWithoutData(PushNotificationRequest request) {
        try {
            Message message = getPreconfiguredMessageWithoutData(request);
            String response = sendAndGetResponse(message);
            log.debug("Notification: ", "Sent message without data. Topic: " + /*request.getTopic() +*/ ", " + response);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private Message getPreconfiguredMessageWithoutData(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setTopic(request.getTopic())
                .build();
    }

    @Override
    public void sendPushNotificationToToken(PushNotificationRequest request) {
        try {
            Message message = getPreconfiguredMessageToToken(request);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(message);
            String response = sendAndGetResponse(message);
            log.debug("Notification : ", "Sent message to token. Device token: " + request.getToken() + ", " + response + " msg " + jsonOutput);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private Message getPreconfiguredMessageToToken(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getToken())
                .build();
    }

    @Override
    public Map<String, String> getPayloadData() {
        Map<String, String> pushData = new HashMap<>();
        pushData.put("messageId", "msgid");
        pushData.put("text", "txt");
        pushData.put("user", "pankaj singh");
        return pushData;
    }

    @Override
    public Map<String, String> getPayloadDataForChat() {

        ChatMessageDetails chatMessageDetails = new ChatMessageDetails();
        chatMessageDetails.setMessage("Hi, There");
        chatMessageDetails.setMessageType(MessageType.TEXT);
        chatMessageDetails.setRead(true);
        chatMessageDetails.setUnReadMessageCount(20L);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(chatMessageDetails);

        Map<String, String> pushData = new HashMap<>();
        pushData.put("message", jsonOutput);
//        pushData.put("messageType", "TEXT");
//        pushData.put("isRead", String.valueOf(false));
//        pushData.put("unReadMessageCount", String.valueOf(20));
        return pushData;
    }

    @Override
    public BaseWrapper getNotificationList(Pageable pageable) {
        Users user = authorizationService.fetchLoggedInUser();
        if(pageable.isPaged()){
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("modifiedTs").descending());
        }else{
            pageable = PageRequest.of(0,10,Sort.by("modifiedTs").descending());
        }
        Page<UserNotification> userNotificationPage = userNotificationRepository.findByUsers_UserIdAndIsCleared(user.getUserId(),false,pageable);
        List<UserNotification> userNotificationList = null;
        List<UserNotificationDTO> userNotificationDTOList = new ArrayList<>();
        Set<Long> unreadMessageIds = new TreeSet<>();
        if(userNotificationPage.hasContent()){
            userNotificationList = userNotificationPage.getContent();
            for(UserNotification notification : userNotificationList){
                if(false == notification.isRead()) {
                    unreadMessageIds.add(notification.getId());
                }
                userNotificationDTOList.add(new UserNotificationDTO(notification));
            }
        }
        if(!unreadMessageIds.isEmpty()){
            log.debug("update is read for unread notification list size = {}",unreadMessageIds.size());
            int rowUpdate = userNotificationRepository.updateIsRead(unreadMessageIds);
        }
        Pagination pagination = new Pagination(userNotificationDTOList,userNotificationDTOList.size(),pageable);

        return new BaseWrapper(userNotificationDTOList,pagination);
    }

    @Override
    public BaseWrapper getClearedAllNotification() {

        Users user = authorizationService.fetchLoggedInUser();

        int rowsUpdated = userNotificationRepository.updateIsClear(user.getUserId());

        return new BaseWrapper(rowsUpdated + " notification are cleared");
    }

    @Override
    public BaseWrapper getClearNotificationById(Set<Long> notificationId) throws ServicesException {

        Users user = authorizationService.fetchLoggedInUser();

        int rowUpdated = userNotificationRepository.getClearNotificationById(notificationId);

        if(rowUpdated != 0){
            return new BaseWrapper(rowUpdated +" notifications cleared");
        }else{
            throw new ServicesException(GeneralErrorCodes.ERR_GENERIC_ERROR_MSSG.value());
        }

    }

}
