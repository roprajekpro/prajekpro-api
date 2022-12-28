package com.prajekpro.api.dto;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class OtpDTO {

    @ApiParam(value = "username will be either emailID or contactNo")
    private String username;
    @ApiParam(value = "otp will be 4 digit code sent to either emailID or contactNo")
    private String otp;
}
