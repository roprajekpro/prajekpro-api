package com.prajekpro.api.domain;

import com.prajekpro.api.dto.ChatMessageDetailsDTO;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "chat_thread_details")
public class ChatThreadDetails extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_FROM")
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "FK_TO")
    private Users receiver;


    public ChatThreadDetails(Long chatThreadId) {
        this.id = chatThreadId;
    }

    public ChatThreadDetails(ChatMessageDetailsDTO request) {
        Users from = new Users(request.getSender());
        this.sender = from;

        Users to = new Users(request.getReceiver());
        this.receiver = to;

    }

    public ChatThreadDetails(Users sender, Users receiver) {

        this.sender = sender;


        this.receiver = receiver;
    }

    /*@OneToMany(targetEntity = ChatMessageDetails.class, mappedBy = "chatThreadDetails",cascade = CascadeType.ALL)
    public List<ChatMessageDetails> chatMessageDetailsList;
*/
}
