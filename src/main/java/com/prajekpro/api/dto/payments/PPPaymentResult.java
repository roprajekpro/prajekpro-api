package com.prajekpro.api.dto.payments;

import com.prajekpro.api.enums.PPPaymentResponseType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class PPPaymentResult implements Serializable {
    private static final long serialVersionUID = -2162200099473694532L;

    public Client client;
    public PPPaymentResponse response;
    public PPPaymentResponseType responseType;
    public String transactionDesc;
    public String transactionId;
    public String transactionDt;
    public String transactionCurrency;
    public String transactionAmt;
}
