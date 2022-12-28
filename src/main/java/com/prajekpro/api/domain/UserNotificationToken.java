package com.prajekpro.api.domain;

import com.safalyatech.common.domains.Auditable;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "user_notification_tokens")
public class UserNotificationToken extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "DEVICE_ID")
    private String deviceId;

    @Column(name = "TOKEN")
    private String token;

    public UserNotificationToken(String userId, String deviceId, String token) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.token = token;
    }
}
