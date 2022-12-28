package com.prajekpro.api.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.ChatMessageDetailsDTO;
import com.prajekpro.api.dto.ChatThreadDetailsDTO;
import com.prajekpro.api.dto.PushNotificationRequest;
import com.prajekpro.api.enums.GeneralErrorCodes;
import com.prajekpro.api.enums.MessageType;
import com.prajekpro.api.enums.NotificationType;
import com.prajekpro.api.repository.ChatMessageDetailsRepository;
import com.prajekpro.api.repository.ChatThreadDetailsRepository;
import com.prajekpro.api.repository.PushNotificationRepository;
import com.prajekpro.api.service.AuthorizationService;
import com.prajekpro.api.service.ChatService;
import com.prajekpro.api.service.FileUploadService;
import com.prajekpro.api.service.PushNotificationService;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.dto.DownloadImageDTO;
import com.safalyatech.common.dto.FileDetailsDTO;
import com.safalyatech.common.enums.ActiveStatus;
import com.safalyatech.common.exception.ServicesException;
import com.safalyatech.common.repository.UsersRepository;
import com.safalyatech.common.utility.CheckUtil;
import com.safalyatech.common.utility.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class ChatServiceImpl implements ChatService {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private ChatMessageDetailsRepository chatMessageDetailsRepository;
    @Autowired
    private ChatThreadDetailsRepository chatThreadDetailsRepository;
    @Autowired
    private PushNotificationRepository pushNotificationRepository;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private FileUploadService fileUploadService;

    @Value("${file.upload.path}")
    private String fileUploadPath;
    @Autowired
    private UsersRepository userRepository;

    @Override
    public BaseWrapper getThreadDetailsList(Pageable pageable) throws ServicesException {
        List<ChatThreadDetailsDTO> chatThreadDetailsDTOList = new ArrayList<>();
        Users user = authorizationService.fetchLoggedInUser();
        // String userId = authorizationService.fetchLoggedInUser().getUserId();
        log.debug("userId = {}", user.getUserId());
        Sort sort = Sort.by("modifiedTs").descending();

        if (!CheckUtil.hasValue(pageable)) pageable = PageRequest.of(0, 20, sort);
        else pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<ChatThreadDetails> chatThreadDetailsPage = chatThreadDetailsRepository.findAllByReceiverOrSender(user.getUserId(), pageable);

        if (!chatThreadDetailsPage.hasContent()) {
            Pagination pagination = new Pagination(chatThreadDetailsDTOList, 0, pageable);
            return new BaseWrapper(chatThreadDetailsDTOList, pagination);
        }

        List<ChatThreadDetails> chatThreadDetailsList = chatThreadDetailsPage.getContent();
        log.debug("is chatThreadDetailsList list is empty ={}", chatThreadDetailsList.isEmpty());
        List<Long> threadIds = new ArrayList<>();

        for (ChatThreadDetails threadDetails : chatThreadDetailsList) {
            threadIds.add(threadDetails.getId());
        }

        // for last message and last message date
        List<ChatMessageDetails> lastMessageList = chatMessageDetailsRepository.findByChatThreadDetails_IdAndModifiedTs(threadIds);

        log.debug("is lastMessage list is empty ={}", lastMessageList.isEmpty());

        HashMap<Long, ChatMessageDetails> lastMessageDetails = new LinkedHashMap<>();

        for (ChatMessageDetails messageDetails : lastMessageList) {
            lastMessageDetails.put(messageDetails.getChatThreadDetails().getId(), messageDetails);
        }

        log.debug("hash map ={}", lastMessageDetails);

        //for unread message count

       /* List<ChatMessageDetails> unReadMessage = chatMessageDetailsRepository.findByIsRead(threadIds);
        if(!CheckUtil.hasValue(unReadMessage)){
            log.debug("no un read message list ");
        }*/


        //TODO:optimise logic

        for (ChatThreadDetails thread : chatThreadDetailsList) {

            ChatMessageDetails lastMessage = lastMessageDetails.get(thread.getId());
            if (CheckUtil.hasValue(lastMessage)) {
                Integer unreadMessageCount = chatMessageDetailsRepository.countUnreadMessage(thread.getId(), user.getUserId());
                unreadMessageCount = CheckUtil.hasValue(unreadMessageCount) ? unreadMessageCount : 0;

                if (!thread.getSender().getUserId().equals(user.getUserId())) {
                    thread.setReceiver(thread.getSender());
                    thread.setSender(user);
                }

                chatThreadDetailsDTOList.add(new ChatThreadDetailsDTO(thread, lastMessage, unreadMessageCount));
            }
        }

        log.debug("chat threads list is empty = {}", chatThreadDetailsDTOList.isEmpty());

        Comparator<ChatThreadDetailsDTO> compareByLastMessage = Comparator.comparing(ChatThreadDetailsDTO::getTimeOfLastMessage);
        Collections.sort(chatThreadDetailsDTOList, compareByLastMessage.reversed());

        Pagination pagination = new Pagination(chatThreadDetailsDTOList, chatThreadDetailsPage.getTotalElements(), pageable);
        return new BaseWrapper(chatThreadDetailsDTOList, pagination);
    }

    @Override
    public BaseWrapper getMessageDetailsList(Long chatThreadId, Pageable pageable) throws ServicesException {

        if (chatThreadId == null || chatThreadId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        if (pageable.isPaged()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("modifiedTs").descending());
        } else {
            pageable = PageRequest.of(0, 20, Sort.by("modifiedTs").descending());
        }
        Page<ChatMessageDetails> chatMessageDetailsPage = chatMessageDetailsRepository.findByChatThreadDetails_Id(chatThreadId, pageable);
        List<ChatMessageDetailsDTO> chatMessageDetailsDTOList = new ArrayList<>();
        long totalElems = 0;
        if (chatMessageDetailsPage.hasContent()) {

            List<ChatMessageDetails> chatMessageDetailsList = chatMessageDetailsPage.getContent();

            for (ChatMessageDetails messages : chatMessageDetailsList) {
                chatMessageDetailsDTOList.add(new ChatMessageDetailsDTO(messages));
            }
            totalElems = chatMessageDetailsPage.getTotalElements();
        }

        Comparator<ChatMessageDetailsDTO> compareByLastMessage = (ChatMessageDetailsDTO o1, ChatMessageDetailsDTO o2) -> o1.getMessageDateTime().compareTo(o2.getMessageDateTime());
        Collections.sort(chatMessageDetailsDTOList, compareByLastMessage);

        Pagination pagination = new Pagination(chatMessageDetailsDTOList, totalElems, pageable);
        return new BaseWrapper(chatMessageDetailsDTOList, pagination);
    }

    @Override
    public BaseWrapper sendMessage(Long chatThreadId, ChatMessageDetailsDTO request) throws ServicesException {

        Users user = authorizationService.fetchLoggedInUser();

        log.debug("before message save");
        request.setMessageType(MessageType.TEXT);
        ChatMessageDetails chatMessageDetails = new ChatMessageDetails(request, chatThreadId);
        chatMessageDetails.setSender(user);
        Optional<Users> receiverUser = userRepository.findById(request.getReceiver());
        receiverUser.ifPresent(chatMessageDetails::setReceiver);
        chatMessageDetails.updateAuditableFields(true, user.getEmailId(), ActiveStatus.ACTIVE.value());
        chatMessageDetailsRepository.save(chatMessageDetails);
        log.debug("after message saved");

        chatMessageDetails.setMessageText(chatMessageDetails.getMessage());

        //Send Chat Notification
        sendChatNotification(chatMessageDetails);

        return new BaseWrapper(chatMessageDetails.getId());

    }

    private void sendChatNotification(ChatMessageDetails chatMessageDetails) {
        List<String> tokenListByUserId = pushNotificationRepository.findTokenListByUserId(chatMessageDetails.getReceiver().getUserId()/*authorizationService.fetchLoggedInUser().getUserId()*/);
        if (!tokenListByUserId.isEmpty())
            pushNotificationService.
                    sendPushNotificationToMultipleToken(getPayloadDataForChat(chatMessageDetails),
                            new PushNotificationRequest(
                                    chatMessageDetails.getSender().getFullName(), chatMessageDetails.getMessage(), tokenListByUserId, NotificationType.CHAT));
    }

    public Map<String, String> getPayloadDataForChat(ChatMessageDetails chatMessageDetails) {

//        ChatNotificationDTO chatNotificationDTO = new ChatNotificationDTO(chatMessageDetails.getSender().getUserId(), chatMessageDetails.getSender().getFullName(),
//                chatMessageDetails.getSender().getIsOnline(), chatMessageDetails.getReceiver().getUserId(), chatMessageDetails.getReceiver().getFullName(),
//                chatMessageDetails.getReceiver().getIsOnline(), chatMessageDetails.isRead(), chatMessageDetails.getMessageType());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(chatMessageDetails);

        Map<String, String> pushData = new HashMap<>();
        pushData.put("message", jsonOutput);
        pushData.put("fromUserId", chatMessageDetails.getSender().getUserId());
        pushData.put("toUserId", chatMessageDetails.getReceiver().getUserId());
        return pushData;
    }


    @Override
    public BaseWrapper updateIsRead(Set<Long> messageIds) throws ServicesException {

        Users user = authorizationService.fetchLoggedInUser();

        if (!CheckUtil.hasValue(messageIds)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        int rowsUpdated = chatMessageDetailsRepository.updateIsRead(messageIds, user.getUserId());

        if (rowsUpdated != messageIds.size()) {
            throw new ServicesException(GeneralErrorCodes.ERR_GENERIC_ERROR_MSSG.value());
        }

        return new BaseWrapper(rowsUpdated + " records updated successfully");
    }

    @Override
    public BaseWrapper createNewThread(String receiverId) throws ServicesException {

        Users sender = authorizationService.fetchLoggedInUser();

        if (null == receiverId) {

            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        log.debug(" sender = {} and receiver = {}", sender.getUserId(), receiverId);

        ChatThreadDetails chatThreadDetails = chatThreadDetailsRepository.findBySenderAndReceiver(sender.getUserId(), receiverId);

        if (!CheckUtil.hasValue(chatThreadDetails)) {
            log.debug("no thread found so created new one");
            Optional<Users> receiverUser = userRepository.findById(receiverId);
            Users receiver = null;
            if (receiverUser.isPresent())
                receiver = receiverUser.get();

            chatThreadDetails = new ChatThreadDetails(sender, receiver);
            chatThreadDetails.updateAuditableFields(true, sender.getEmailId(), ActiveStatus.ACTIVE.value());
            chatThreadDetailsRepository.save(chatThreadDetails);
            log.debug("new thread created with thread ID = {}", chatThreadDetails.getId());
        }
        log.debug("thread id = {}", chatThreadDetails.getId());
        ChatThreadDetailsDTO chatThreadDetailsDTO = new ChatThreadDetailsDTO(chatThreadDetails);

        return new BaseWrapper(chatThreadDetailsDTO);
    }

    @Override
    public BaseWrapper markAllMessageRead(Long chatThreadId) throws ServicesException {

        if (!CheckUtil.hasValue(chatThreadId)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        int rowsUpdated = chatMessageDetailsRepository.markAllRead(chatThreadId);

        return new BaseWrapper(rowsUpdated + " messages marked as read");
    }

    @Override
    public BaseWrapper sendDocument(Long chatThreadId, MultipartFile file, String receiverId) throws ServicesException, IOException {

        log.debug("receiver Id = {}", receiverId);

        ChatMessageDetailsDTO chatMessageDetailsDTO = new ChatMessageDetailsDTO();
        chatMessageDetailsDTO.setReceiver(receiverId);
        ChatThreadDetails chatThreadDetails = checkForChatThread(chatThreadId, chatMessageDetailsDTO);

        Users user = authorizationService.fetchLoggedInUser();

        log.debug("before message save");
        FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);
        log.debug("file upload path = {} ", fileDetailsDTO.getFilePath());

        chatMessageDetailsDTO.setMessage(fileDetailsDTO.getFileName());
        chatMessageDetailsDTO.setMetaData(new ObjectMapper().writeValueAsString(fileDetailsDTO));
        chatMessageDetailsDTO.setMessageType(MessageType.DOCUMENT);

        ChatMessageDetails chatMessageDetails = new ChatMessageDetails(chatMessageDetailsDTO, chatThreadId);
        chatMessageDetails.updateAuditableFields(true, user.getEmailId(), ActiveStatus.ACTIVE.value());

        chatMessageDetailsRepository.save(chatMessageDetails);
        log.debug("after message saved");

        //Send Chat Notification
        sendChatNotification(chatMessageDetails);

        return new BaseWrapper(chatMessageDetails.getId());

    }

    private ChatThreadDetails checkForChatThread(Long chatThreadId, ChatMessageDetailsDTO request) throws ServicesException {

        if (!CheckUtil.hasValue(request)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Users user = authorizationService.fetchLoggedInUser();
        request.setSender(user.getUserId());

        ChatThreadDetails chatThreadDetails = null;
        if (null == chatThreadId || chatThreadId <= 0l) {
            log.debug("when thread id is zero or null");
            log.debug(" sender = {} and receiver = {}", request.getSender(), request.getReceiver());

            chatThreadDetails = chatThreadDetailsRepository.findBySenderAndReceiver(request.getSender(), request.getReceiver());

            if (!CheckUtil.hasValue(chatThreadDetails)) {
                log.debug("no thread found so created new one");
                chatThreadDetails = new ChatThreadDetails(request);
                chatThreadDetails.updateAuditableFields(true, user.getEmailId(), ActiveStatus.ACTIVE.value());
                chatThreadDetailsRepository.save(chatThreadDetails);
                log.debug("new thread created with thread ID = {}", chatThreadDetails.getId());
            }
            chatThreadId = chatThreadDetails.getId();
        }

        return chatThreadDetails;
    }

    @Override
    public DownloadImageDTO getMessageAttachmentInfo(String id) throws IOException {

        ChatMessageDetails chatMessageDetails = chatMessageDetailsRepository.fetchById(Long.parseLong(id));
        String metaData = chatMessageDetails.getMetaData();
        FileDetailsDTO fileDetailsDTO = new ObjectMapper().readValue(metaData, FileDetailsDTO.class);

        DownloadImageDTO downloadImageDTO = new DownloadImageDTO();
        downloadImageDTO.setImgExtn(fileDetailsDTO.getFileExtension());
        downloadImageDTO.setDisplayName(fileDetailsDTO.getFileName());
        downloadImageDTO.setFilePath(fileDetailsDTO.getPathToFile());

        return downloadImageDTO;
    }
}
