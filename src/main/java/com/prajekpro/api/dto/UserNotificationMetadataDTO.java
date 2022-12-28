package com.prajekpro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationMetadataDTO {
    private String notificationBody;
    private String notificationTime;
}
