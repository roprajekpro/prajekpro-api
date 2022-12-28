package com.prajekpro.api.dto;

import com.prajekpro.api.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatNotificationDTO {

    private String senderId;
    private String senderName;
    private boolean isSenderOnline;
    private String receiverId;
    private String receiverName;
    private boolean isReceiverOnline;
    private boolean isRead;
    private MessageType messageType;

}
