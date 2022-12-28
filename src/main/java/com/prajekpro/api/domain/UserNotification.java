package com.prajekpro.api.domain;

import com.prajekpro.api.enums.NotificationType;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

import com.safalyatech.common.domains.Auditable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user_notification")
public class UserNotification extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_TO")
    private Users users;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "NOTIFICATION_TYPE")
    private NotificationType notificationType;

    @Column(name = "META_DATA")
    private String metaData;

    @Column(name = "IS_READ")
    private boolean isRead;

    @Column(name = "IS_CLEARED")
    private boolean isCleared;

    public UserNotification(Users users, String title, NotificationType notificationType, String metaData, boolean isRead, boolean isCleared) {
        this.users = users;
        this.title = title;
        this.notificationType = notificationType;
        this.metaData = metaData;
        this.isRead = isRead;
        this.isCleared = isCleared;
    }
}
