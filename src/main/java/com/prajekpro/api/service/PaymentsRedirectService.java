package com.prajekpro.api.service;

import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.service.exception.ApiException;
import com.prajekpro.api.dto.payments.PPPaymentResult;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;

import java.io.IOException;
import java.text.*;

public interface PaymentsRedirectService {
    BaseWrapper getPaymentDetailsAndResult(String redirectResult) throws IOException, ApiException, ServicesException, ParseException;

    BaseWrapper getPaymentDetailsAndResult(String redirectResult, String transactionId) throws IOException, ApiException, ServicesException, ParseException;

    void doHandlePaymentResult(Integer txdId, PaymentsDetailsResponse paymentsDetailsResponse, boolean doRedirect) throws IOException, ServicesException, ParseException;

    BaseWrapper updateTransactionDetailsByPaymentResponse(PPPaymentResult ppPaymentResult) throws IOException, ServicesException, ServicesException, ParseException;
}
