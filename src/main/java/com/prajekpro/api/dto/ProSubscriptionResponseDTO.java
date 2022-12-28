package com.prajekpro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProSubscriptionResponseDTO {
    private String proName;
    private String applicationNo;
    private String dateOfPurchase;
    private Double amountPaid;
    private String currencySymbol;

}
