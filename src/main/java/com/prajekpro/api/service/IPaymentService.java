package com.prajekpro.api.service;

import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.exception.ApiException;
import com.prajekpro.api.dto.MakePaymentResponseWrapper;
import com.prajekpro.api.enums.TransactionType;
import com.safalyatech.common.constants.GlobalConstants;
import com.safalyatech.common.utility.CommonUtility;

import java.io.IOException;

public interface IPaymentService {

    PaymentsResponse getCheckoutUrl(String currencyCode, long orderAmount, String orderReference, String returnUrl) throws IOException, ApiException;

    PaymentsDetailsResponse getPaymentDetails(String redirectResult) throws IOException, ApiException;

    public default MakePaymentResponseWrapper getMakePaymentResponseWrapper(Integer transactionId, TransactionType transactionType,
                                                                            String dummyCheckoutUrl, boolean enablePaymentPortal,
                                                                            String returnUrl, String successUrl, String failureUrl,
                                                                            String currencyCode, long orderAmount) throws IOException, ApiException {
        String convertedTransactionID = CommonUtility.convertTrxIdIntoString(transactionId);
        //Call make Payment API
        String checkoutUrl = dummyCheckoutUrl + convertedTransactionID;
        //Only renew PRO subscription uses WALLET as mode of payment. All other payment options use GCASH as mode of payment
        String paymentMethodType = transactionType != TransactionType.RENEW_SUBSCRIPTION ? GlobalConstants.PaymentMethods.PAYPAL : GlobalConstants.PaymentMethods.WALLET;
        String resultCode = PaymentsResponse.ResultCodeEnum.AUTHORISED.getValue();
        MakePaymentResponseWrapper responseWrapper = new MakePaymentResponseWrapper();

        if (enablePaymentPortal && (transactionType != TransactionType.RENEW_SUBSCRIPTION)) {
            PaymentsResponse response = getCheckoutUrl(currencyCode, orderAmount, convertedTransactionID, returnUrl);
            checkoutUrl = response.getAction().getUrl();
            paymentMethodType = response.getAction().getPaymentMethodType();
            resultCode = response.getResultCode().getValue();
        }

        responseWrapper.setCheckOutUrl(checkoutUrl);
        responseWrapper.setSuccessUrl(successUrl);
        responseWrapper.setFailureUrl(failureUrl);
        responseWrapper.setExpectedResultCode(resultCode);
        responseWrapper.setPaymentMethodType(paymentMethodType);

        return responseWrapper;
    }


    //BaseWrapper getPaymentDetails(String redirectResult) throws ServicesException;
}
