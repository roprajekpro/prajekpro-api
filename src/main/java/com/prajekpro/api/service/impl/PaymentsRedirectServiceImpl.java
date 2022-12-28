package com.prajekpro.api.service.impl;

import com.adyen.model.checkout.*;
import com.adyen.service.exception.*;
import com.prajekpro.api.domain.PaymentDetails;
import com.prajekpro.api.dto.payments.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.prajekpro.api.util.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import javax.servlet.http.*;
import java.io.*;
import java.text.*;

@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class PaymentsRedirectServiceImpl implements PaymentsRedirectService {

    @Autowired
    private IPaymentService iPaymentService;

    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private HttpServletResponse servletResponse;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private WalletService walletService;

    @Value("${payments.gcash.successURL}")
    private String successURL;

    @Value("${payments.gcash.failureURL}")
    private String failureURL;


    @Override
    public BaseWrapper getPaymentDetailsAndResult(String redirectResult) throws IOException, ApiException, ServicesException, ParseException {
        PaymentsDetailsResponse paymentsDetailsResponse = iPaymentService.getPaymentDetails(redirectResult);

        Integer txdId = CommonUtility.getTransactionIdFromPrefix(paymentsDetailsResponse.getMerchantReference());
        doHandlePaymentResult(txdId, paymentsDetailsResponse, true);

        return new BaseWrapper();
    }

    @Override
    public void doHandlePaymentResult(Integer txdId, PaymentsDetailsResponse paymentsDetailsResponse, boolean doRedirect) throws IOException, ServicesException, ParseException {
        final String TAG_METHOD_NAME = "doHandlePaymentResult";

        //Get payment details by txnID
        PaymentDetails paymentDetails = paymentDetailsRepository.getPaymentDetailsByTxdId(txdId);

        //Add other details inside payment details table
        log.debug("paymentsDetailsResponse = {}", paymentsDetailsResponse.toString());
        paymentDetails.setPspReference(paymentsDetailsResponse.getPspReference());
        paymentDetails.setFinalResultCode(paymentsDetailsResponse.getResultCode().getValue());
        paymentDetails.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());

        //Update payment status
        PPPaymentResponseType ppPaymentResponseFromResultCode = getPPPaymentResponseFromResultCode(paymentsDetailsResponse.getResultCode());
        String paymentStatus = paymentsDetailsResponse.getResultCode() == PaymentsResponse.ResultCodeEnum.AUTHORISED ? PaymentStatus.COMPLETED.name() :
                PaymentStatus.FAILED.name();
        paymentDetails.setPaymentStatus(paymentStatus);

        //Save final payment details
        paymentDetailsRepository.save(paymentDetails);

        log.info(GlobalConstants.LOG.INFO, TAG_METHOD_NAME, getClass().getName(), "transactionType = " + paymentDetails.getTransactionType());
        log.debug("txdId = {}, paymentDetails = {}, ppPaymentResponseFromResultCode = {}", txdId, paymentDetails, ppPaymentResponseFromResultCode);
        switch (paymentDetails.getTransactionType()) {
            case SUBSCRIPTION:
            case RENEW_SUBSCRIPTION:
                //update subscription related tables and send login information
                subscriptionService.updateSubscriptionTables(txdId, paymentDetails, ppPaymentResponseFromResultCode);
                break;

            case APPOINTMENT:
                appointmentService.updateAppointmentTables(txdId, paymentDetails, ppPaymentResponseFromResultCode);
                break;

            case APPOINTMENT_CANCEL:
            case CUST_APPOINTMENT_CANCEL:
                appointmentService.updateAppointmentCancellationTables(txdId, paymentDetails, ppPaymentResponseFromResultCode);
                break;

            case LOAD_WALLET:
                walletService.updateWalletAmountTables(txdId, paymentDetails, ppPaymentResponseFromResultCode);
                break;
        }

        if (doRedirect)
            servletResponse.sendRedirect(successURL);
    }

    private PPPaymentResponseType getPPPaymentResponseFromResultCode(PaymentsResponse.ResultCodeEnum resultCode) {
        switch (resultCode) {
            case AUTHORISED:
                return PPPaymentResponseType.SUCCESS;

            case ERROR:
            case CANCELLED:
            case REFUSED:
                return PPPaymentResponseType.FAILURE;

            default:
                return PPPaymentResponseType.FAILURE;
        }
    }

    /*This method is created get dummy payment details by passing dummy result code but updated transactionId.
     * NOTE:USE THIS METHOD JUST AS ALTERNATIVE TO ALIPAY APIS AND REMOVE IT ONCE CODE DEPLOYED TO PRODUCTION*/

    @Override
    public BaseWrapper getPaymentDetailsAndResult(String redirectResult, String transactionId) throws IOException, ApiException, ServicesException, ParseException {

        Integer txdId = CommonUtility.getTransactionIdFromPrefix(transactionId);
        doHandlePaymentResult(txdId, iPaymentService.getPaymentDetails(redirectResult), true);

        return null;
    }

    @Override
    public BaseWrapper updateTransactionDetailsByPaymentResponse(PPPaymentResult ppPaymentResult) throws IOException, ServicesException, ServicesException, ParseException {
        if (!CheckUtil.hasValue(ppPaymentResult.responseType))
            throw new ServicesException(704);

        PaymentsResponse.ResultCodeEnum resultCodeEnum = ppPaymentResult.responseType == PPPaymentResponseType.SUCCESS ? PaymentsResponse.ResultCodeEnum.AUTHORISED : PaymentsResponse.ResultCodeEnum.ERROR;
        Integer txnId = Integer.parseInt(ppPaymentResult.getTransactionId());
        PaymentsDetailsResponse paymentsDetailsResponse = PPUtils.getPaymentDetailsResponse(txnId, resultCodeEnum);
        doHandlePaymentResult(txnId, paymentsDetailsResponse, false);

        return new BaseWrapper();
    }
}
