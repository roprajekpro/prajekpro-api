package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ChatMessageDetails;
import com.prajekpro.api.enums.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatMessageDetailsDTO {

    private Long messageId;
    private Long threadId;
    private Boolean isOnline;
    private String sender;
    private String receiver;
    private String message;
    private MessageType messageType;
    private Integer messageTypeId;
    private boolean isRead;
    private Long messageDateTime;
    private String metaData;

    public ChatMessageDetailsDTO(ChatMessageDetails message) {
        this.messageId = message.getId();
        this.threadId = message.getChatThreadDetails().getId();
        // this.isOnline = message.getReceiver().getIsOnline();
        this.sender = message.getSender().getUserId();
        this.receiver = message.getReceiver().getUserId();
        this.message = message.getMessage();
        this.messageTypeId = message.getMessageType().value();
        this.messageType = message.getMessageType();

        this.isRead = message.isRead();
        this.messageDateTime = message.getModifiedTs();
        this.metaData = message.getMetaData();
    }
}
