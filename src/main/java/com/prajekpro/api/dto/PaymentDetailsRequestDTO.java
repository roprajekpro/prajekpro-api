package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaymentDetailsRequestDTO {

    private PaymentRedirectDetailsDTO details;
}
