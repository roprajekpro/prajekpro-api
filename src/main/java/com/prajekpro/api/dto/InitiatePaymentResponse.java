package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class InitiatePaymentResponse implements Serializable {

    private static final long serialVersionUID = 5807335062177220968L;
    private String payPalEnvironmentProduction;
    private String payPalEnvironmentSandbox;
    private String transactionDesc;
    private String transactionId;
    private long transactionDt;
    private String transactionCurrency;
    private String transactionAmt;
    private String paymentEnvironment;
    private boolean paymentRequired = true;

    public InitiatePaymentResponse(String payPalEnvironmentSandboxClientKey, String payPalEnvironmentProductionClientKey,
                                   String paymentEnvironment, String transactionDesc, String transactionId, long transactionDt,
                                   String transactionCurrency, String transactionAmt) {
        if (Double.parseDouble(transactionAmt) > 0) {
            this.payPalEnvironmentProduction = payPalEnvironmentProductionClientKey;
            this.payPalEnvironmentSandbox = payPalEnvironmentSandboxClientKey;
            this.transactionDesc = transactionDesc;
            this.transactionId = transactionId;
            this.transactionDt = transactionDt;
            this.transactionCurrency = transactionCurrency;
            this.transactionAmt = transactionAmt;
            this.paymentEnvironment = paymentEnvironment;
        } else
            this.paymentRequired = false;
    }

    public InitiatePaymentResponse(boolean paymentRequired) {
        this.paymentRequired = paymentRequired;
    }
}
