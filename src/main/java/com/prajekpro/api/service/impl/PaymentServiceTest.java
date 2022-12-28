package com.prajekpro.api.service.impl;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.*;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.prajekpro.api.constants.PaymentGatewayConstants;
import com.prajekpro.api.dto.MakePaymentResponseWrapper;
import com.prajekpro.api.enums.TransactionType;
import com.prajekpro.api.service.IPaymentService;
import com.prajekpro.api.service.PublicService;

import com.safalyatech.common.constants.GlobalConstants;
import com.safalyatech.common.repository.UsersRepository;
import com.safalyatech.common.utility.CommonUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Service
@Profile("!prod")
@Transactional(rollbackFor = Throwable.class)
public class PaymentServiceTest implements IPaymentService {

    @Value("${payments.gcash.apiKey}")
    private String apiKey;
    @Value("${payments.gcash.merchantAccount.name}")
    private String merchantAccountName;


    @Override
    public PaymentsResponse getCheckoutUrl(String currencyCode, long orderAmount, String orderReference, String returnUrl) throws IOException, ApiException {

        // Set YOUR_X-API-KEY with the API key from the Customer Area.
        // Change to Environment.LIVE and add the Live URL prefix when you're ready to accept live payments.
        Client client = new Client(apiKey, Environment.TEST);
        Checkout checkout = new Checkout(client);

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

        return checkout.payments(paymentsRequest);
    }

    @Override
    public PaymentsDetailsResponse getPaymentDetails(String redirectResult) throws IOException, ApiException {
        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        paymentsDetailsRequest.setDetails(Collections.singletonMap(PaymentGatewayConstants.REDIRECT_RESULT, redirectResult));

        // Set YOUR_X-API-KEY with the API key from the Customer Area.
        // Change to Environment.LIVE and add the Live URL prefix when you're ready to accept live payments.
        Client client = new Client(apiKey, Environment.TEST);
        Checkout checkout = new Checkout(client);
        return checkout.paymentsDetails(paymentsDetailsRequest);
    }

    /* @Override
    public BaseWrapper getPaymentDetails(String redirectResult) throws ServicesException {

        // create request entity for /payments/details
        PaymentDetailsRequestDTO requestEntity = new PaymentDetailsRequestDTO();
        PaymentRedirectDetailsDTO details = new PaymentRedirectDetailsDTO();
        details.setRedirectResult(redirectResult);
        requestEntity.setDetails(details);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<PaymentDetailsRequestDTO> entity = new HttpEntity<PaymentDetailsRequestDTO>(requestEntity, headers);

        PaymentDetailsResponseDTO detailsResponseDTO = restTemplate.exchange(
                "https://docs.adyen.com/api-explorer/#/CheckoutService/v67/post/payments/details", HttpMethod.POST, entity, PaymentDetailsResponseDTO.class).getBody();
        Integer txdId = Integer.valueOf(detailsResponseDTO.getMerchantReference());
        PaymentDetails paymentDetails = paymentDetailsRepository.getPaymentDetailsByTxdId(txdId);

        if (detailsResponseDTO.getResultCode().equals(PaymentResultCode.Authorised)) {
            switch (paymentDetails.getTransactionType()) {
                case SUBSCRIPTION:

                    // add other details inside payment details table
                    paymentDetails.setPspReference(detailsResponseDTO.getPspReference());
                    paymentDetails.setFinalResultCode(detailsResponseDTO.getResultCode());
                    paymentDetails.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
                    paymentDetailsRepository.save(paymentDetails);

                    // update proSubscription table
                    ProSubscription proSubscription = proSubscriptionRepository.getSubscriptionByTXDId(txdId);
                    proSubscription.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
                    proSubscriptionRepository.save(proSubscription);


                    //update subscription invoice table
                    SubscriptionInvoice subscriptionInvoice = subscriptionInvoiceRepository.getByProSubscriptionId(proSubscription.getId());
                    subscriptionInvoice.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
                    subscriptionInvoiceRepository.save(subscriptionInvoice);

                    //update Wallet Transaction History table
                    WalletTransactionHistory walletTransactionHistory = walletTransactionHistoryRepository.getTransactionHistoryByTXDId(txdId);
                    walletTransactionHistory.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
                    walletTransactionHistoryRepository.save(walletTransactionHistory);

                    //update wallet topup History table
                    WalletTopUpHistory topUpHistory = walletTopUpHistoryRepository.getTopupHistoryByTxdId(txdId);
                    topUpHistory.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
                    walletTopUpHistoryRepository.save(topUpHistory);

                    //update Pro Wallet Details Table
                    ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_Id(paymentDetails.getProDetails().getId());
                    proWalletDetails.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
                    walletDetailsRepository.save(proWalletDetails);

                    //update prajekPro Wallet table
                    PrajekProWalletDetails prajekProWalletDetails = prajekproWalletDetailsRepository.getWalletDetailsByTxdId(txdId);
                    prajekProWalletDetails.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
                    prajekproWalletDetailsRepository.save(prajekProWalletDetails);

                    ProDetails proDetails = proSubscription.getProDetails();
                    MasterSubscription masterSubscription = proSubscription.getMasterSubscription();
                    ProSubscriptionResponseDTO proSubscriptionResponseDTO = new ProSubscriptionResponseDTO();
                    if (proDetails != null) {
                        proSubscriptionResponseDTO.setProName(proDetails.getUserDetails().getFirstNm());
                        proSubscriptionResponseDTO.setApplicationNo(String.valueOf(proDetails.getApplicationNo()));
                    }
                    proSubscriptionResponseDTO.setAmountPaid(masterSubscription.getSubscriptionAmount());
                    proSubscriptionResponseDTO.setDateOfPurchase(proSubscription.getDateOfSubscription());
                    if (masterSubscription != null) {
                        proSubscriptionResponseDTO.setCurrencySymbol(masterSubscription.getCurrency().getSymbol());
                    }

                    Users users = proDetailsRepository.fetchByUserIdIn(proDetails.getId());
                    users.setActiveStatus(ActiveStatus.APPROVAL_PENDING.value());
                    usersRepository.save(users);

                    ProSubscriptionResponseLoginDTO proSubscriptionResponseLoginDTO = new ProSubscriptionResponseLoginDTO(proSubscriptionResponseDTO, publicService.doLogInPro(proDetails.getId()));
                    return new BaseWrapper(proSubscriptionResponseLoginDTO);
            }
        }
        return null;
    }*/
}
