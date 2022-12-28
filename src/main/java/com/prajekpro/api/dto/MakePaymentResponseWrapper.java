package com.prajekpro.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MakePaymentResponseWrapper {

    private String checkOutUrl;
    private String successUrl;
    private String failureUrl;
    @JsonIgnore
    private String paymentMethodType;
    @JsonIgnore
    private String expectedResultCode;
}
