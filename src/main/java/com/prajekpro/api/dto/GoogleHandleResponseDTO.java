package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class GoogleHandleResponseDTO implements Serializable {

    public String accessToken;
    public Integer expires;
    public Integer expiresIn;
    public String email;
    public String userId;
    public String displayName;
    public String familyName;
    public String givenName;
    private String contactNo;
    private final static long serialVersionUID = -38145126096867047L;

}