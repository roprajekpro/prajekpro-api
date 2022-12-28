package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ChatMessageDetails;
import com.prajekpro.api.domain.ChatThreadDetails;
import com.prajekpro.api.enums.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatThreadDetailsDTO {

    private Long threadId;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String receiverName;
    private String lastMessage;
    private String timeOfLastMessage;
    private Boolean isOnline;
    private Integer unreadMessageCount;
    private MessageType messageType;

    public ChatThreadDetailsDTO(ChatThreadDetails thread, ChatMessageDetails lastMessage, Integer unreadMessageCount) {

        this.threadId = thread.getId();
        this.senderId = thread.getSender().getUserId();
        this.senderName = thread.getSender().getFullName();
        this.receiverId = thread.getReceiver().getUserId();
        this.receiverName = thread.getReceiver().getFullName();
        this.lastMessage = lastMessage.getMessage();
        this.timeOfLastMessage = String.valueOf(lastMessage.getModifiedTs());
        this.messageType = lastMessage.getMessageType();
        this.unreadMessageCount = unreadMessageCount;
        this.isOnline = thread.getReceiver().getIsOnline();
    }

    public ChatThreadDetailsDTO(ChatThreadDetails chatThreadDetails) {

        this.threadId = chatThreadDetails.getId();
        this.senderId = chatThreadDetails.getSender().getUserId();
        this.senderName = chatThreadDetails.getSender().getName();
        this.receiverId = chatThreadDetails.getReceiver().getUserId();
        this.receiverName = chatThreadDetails.getReceiver().getName();
        this.isOnline = chatThreadDetails.getReceiver().getIsOnline();
    }
}
