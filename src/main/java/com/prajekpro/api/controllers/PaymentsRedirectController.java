package com.prajekpro.api.controllers;

import com.adyen.service.exception.ApiException;
import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.dto.payments.PPPaymentResult;
import com.prajekpro.api.exception.PPServicesException;
import com.prajekpro.api.service.IPaymentService;
import com.prajekpro.api.service.PaymentsRedirectService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.*;


@Slf4j
@RestController
@RequestMapping(value = {
        RestUrlConstants.PP_PAYMENT
})
@Api(value = "API related to payment integration")
public class PaymentsRedirectController {

    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private PaymentsRedirectService paymentsRedirectService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${payments.gcash.dummyRedirectResult}")
    private String dummyRedirectResult;


   /* @ApiOperation(value = "make Gcash payment and get checkout URL")
    @PostMapping(value = {
            RestUrlConstants.PP_PAYMENT_GCASH
    })
    public BaseWrapper makePayment() throws IOException, ApiException {
        PaymentsResponse response = paymentService.getCheckoutUrl("PHP", 1000L, "MY_ORDER_REF", "https://safalyatech.com/payments/redirectUrl");
        log.debug("makePayment = {}", response.toString());
        return new BaseWrapper(response);
    }*/


    /*This method is created get dummy payment details by passing dummy result code but updated transactionId.
     * NOTE:USE THIS METHOD JUST AS ALTERNATIVE TO ALIPAY APIS AND REMOVE IT ONCE CODE DEPLOYED TO PRODUCTION*/
    @ApiOperation(value = "API as a Checkout Url")
    @GetMapping(value = {RestUrlConstants.PP_PAYMENT_CHECKOUTURL})
    public BaseWrapper getCheckoutUrl(@PathVariable("transactionId") String transactionId) throws IOException, ServicesException, ApiException, ParseException {
        return paymentsRedirectService.getPaymentDetailsAndResult(dummyRedirectResult, transactionId);
    }


    @ApiOperation(value = " API to get redirectResult from payment")
    @GetMapping(value = {RestUrlConstants.PP_PAYMENT_REDIRECTRESULT})
    public BaseWrapper getRedirectResult(@RequestParam String redirectResult) throws IOException, ApiException, ServicesException, ParseException {
        log.debug("redirectResult = {}", redirectResult);
        return paymentsRedirectService.getPaymentDetailsAndResult(redirectResult);
    }


    @ApiOperation(value = " API to update payment results")
    @PostMapping(value = {RestUrlConstants.PP_PAYMENT_RESULTS})
    public BaseWrapper updatePaymentResults(@RequestBody PPPaymentResult ppPaymentResult) throws IOException, ApiException, ServicesException, ServicesException, ParseException {
        log.info("ppPaymentResult = {}", ppPaymentResult);
        return paymentsRedirectService.updateTransactionDetailsByPaymentResponse(ppPaymentResult);
    }
}
