package com.prajekpro.api.domain;

import com.prajekpro.api.dto.ChatMessageDetailsDTO;
import com.prajekpro.api.enums.MessageType;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString

@Entity
@Table(name = "chat_message_details")
public class ChatMessageDetails extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_FROM")
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "FK_TO")
    private Users receiver;

    @ManyToOne
    @JoinColumn(name = "CHAT_THREAD_DETAILS_ID")
    private ChatThreadDetails chatThreadDetails;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "MESSAGE_TYPE")
    private MessageType messageType;

    @Column(name = "IS_READ")
    private boolean isRead = false;

    @Column(name = "META_DATA")
    private String metaData;

    @Transient
    private Long unReadMessageCount;

    @Transient
    private String messageText;

    public ChatMessageDetails(ChatMessageDetailsDTO request, Long chatThreadId) {

        Users from = new Users(request.getSender());
        this.sender = from;

        log.debug("sender = {}", sender.toString());

        Users to = new Users(request.getReceiver());
        this.receiver = to;

        log.debug("receiver = {}", receiver.toString());

        this.message = request.getMessage();
        this.messageType = request.getMessageType();

        ChatThreadDetails chatThread = new ChatThreadDetails(chatThreadId);
        this.chatThreadDetails = chatThread;

        this.metaData = request.getMetaData();
    }

   /* public ChatMessageDetails(Long unReadMessageCount, ChatThreadDetails ChatThreadDetails){

        this.unReadMessageCount = unReadMessageCount;
       // ChatThreadDetails threadDetails = new ChatThreadDetails(chatThreadId);
        this.chatThreadDetails = ChatThreadDetails;

    }*/

}
