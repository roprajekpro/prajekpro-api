package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class FacebookLoginDTO  implements Serializable{
    public String accessToken;
    public String email;
    public String id;
    public String name;
    private String contactNo;
}