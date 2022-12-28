package com.prajekpro.api.helpers;

import com.prajekpro.api.dto.InitiatePaymentResponse;
import com.safalyatech.common.constants.GlobalConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentsHelper {

    @Value("${paypal.environment}")
    private String payPalEnvironment;
    @Value("${paypal.environment.sandbox.client-id}")
    private String payPalEnvironmentSandboxClientKey;
    @Value("${paypal.environment.production.client-id}")
    private String payPalEnvironmentProdClientKey;

    /**
     * @param txnId
     * @param amount
     * @return
     */
    public InitiatePaymentResponse initiatePaymentResponse(Integer txnId, double amount) {
        String transactionIdStr = Integer.toString(txnId);
        return new InitiatePaymentResponse(payPalEnvironmentSandboxClientKey, payPalEnvironmentProdClientKey, payPalEnvironment,
                transactionIdStr, transactionIdStr, System.currentTimeMillis(), GlobalConstants.Currency.PHP, Double.toString(amount));
    }
}
