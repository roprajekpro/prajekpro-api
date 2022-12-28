package com.prajekpro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "username",
        "profileImage",
        "role"
})
public class UserDetailsVO {
    @JsonProperty("username")
    private String username;
    @JsonProperty("profileImage")
    private String profileImage;
    @JsonProperty("role")
    private List<String> role = new ArrayList<String>();

    public UserDetailsVO(Users users) {

        this.username = users.getUsername();
        this.profileImage = users.getUserId();
        this.role = users
                .getRoles()
                .stream()
                .map(
                        ur -> ur.getRoles())
                .collect(
                        Collectors.toList());
    }


}
