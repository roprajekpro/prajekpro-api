package com.prajekpro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProSubscriptionResponseLoginDTO {
    private ProSubscriptionResponseDTO subscription;
    private OAuth2AccessToken login;
}
