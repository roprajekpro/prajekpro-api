package com.prajekpro.api.service.impl;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.*;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.prajekpro.api.constants.PaymentGatewayConstants;
import com.prajekpro.api.dto.PaymentDetailsResponseDTO;
import com.prajekpro.api.service.IPaymentService;
import com.safalyatech.common.dto.BaseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Service
@Profile("prod")
@Transactional(rollbackFor = Throwable.class)
public class PaymentService implements IPaymentService {

    @Value("${payments.gcash.apiKey}")
    private String apiKey;
    @Value("${payments.gcash.live.url.prefix}")
    private String liveUrlPrefix;
    @Value("${payments.gcash.merchantAccount.name}")
    private String merchantAccountName;

    private Checkout checkout;

    public PaymentService() {
        // Set YOUR_X-API-KEY with the API key from the Customer Area.
        // Change to Environment.LIVE and add the Live URL prefix when you're ready to accept live payments.
//        Client client = new Client(apiKey, Environment.LIVE, liveUrlPrefix);
//        Checkout checkout = new Checkout(client);
    }

    @Override
    public PaymentsResponse getCheckoutUrl(String currencyCode, long orderAmount, String orderReference, String returnUrl) throws IOException, ApiException {
        PaymentsRequest paymentsRequest = new PaymentsRequest();
        paymentsRequest.setMerchantAccount(merchantAccountName);

        Amount amount = new Amount();
        amount.setCurrency(currencyCode);
        amount.setValue(orderAmount);
        paymentsRequest.setAmount(amount);

        DefaultPaymentMethodDetails paymentMethodDetails = new DefaultPaymentMethodDetails();
        paymentMethodDetails.setType(PaymentGatewayConstants.PAYMENT_TYPE_GCASH);
        paymentsRequest.setPaymentMethod(paymentMethodDetails);

        paymentsRequest.setReference(orderReference);
        paymentsRequest.setReturnUrl(returnUrl);

        return this.checkout.payments(paymentsRequest);
    }

    @Override
    public PaymentsDetailsResponse getPaymentDetails(String redirectResult) throws IOException, ApiException {
        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        paymentsDetailsRequest.setDetails(Collections.singletonMap(PaymentGatewayConstants.REDIRECT_RESULT, redirectResult));

        return this.checkout.paymentsDetails(paymentsDetailsRequest);
    }
}

