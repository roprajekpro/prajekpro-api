package com.prajekpro.api.service.impl;

import com.adyen.model.checkout.*;
import com.adyen.service.exception.*;
import com.prajekpro.api.constants.*;
import com.prajekpro.api.converters.*;
import com.prajekpro.api.domain.Currency;
import com.prajekpro.api.domain.PaymentDetails;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.exception.*;
import com.prajekpro.api.helpers.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.prajekpro.api.util.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.repository.*;
import com.safalyatech.common.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import javax.transaction.*;
import java.io.*;
import java.math.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private DTOFactory dtoService;
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private PublicService publicService;

    @Autowired
    private WalletTransactionHistoryRepository walletTransactionHistoryRepository;
    @Autowired
    private ProDetailsRepository proDetailsRepository;
    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;
    @Autowired
    private MasterSubscriptionRepository masterSubscriptionRepository;
    @Autowired
    private ProSubscriptionRepository proSubscriptionRepository;
    @Autowired
    private PPLookupRepository ppLookupRepository;
    @Autowired
    private WalletDetailsRepository walletDetailsRepository;
    @Autowired
    private PrajekproWalletDetailsRepository prajekproWalletDetailsRepository;
    @Autowired
    private UsersRepository userRepository;
    @Autowired
    private SubscriptionInvoiceRepository subscriptionInvoiceRepository;
    @Autowired
    private WalletTopUpHistoryRepository walletTopUpHistoryRepository;
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private CouponCodeDetailsRepository couponCodeDetailsRepository;
    @Autowired
    private CouponRedemptionDetailsRepository couponRedemptionDetailsRepository;

    @Value("${payments.gcash.returnUrl}")
    private String returnUrl;
    @Value("${payments.gcash.dummyCheckoutUrl}")
    private String dummyCheckoutUrl;
    //    @Value("${payments.portal.enable}")
//    private boolean enablePaymentPortal;
    @Value("${payments.gcash.successURL}")
    private String successUrl;
    @Value("${payments.gcash.failureURL}")
    private String failureUrl;
    @Value("${paypal.environment.client-id}")
    private String payPalEnvironmentClientKey;

    @Autowired
    private PaymentsHelper paymentHelper;
    @Autowired
    private WalletHelper walletHelper;


    @Override
    public BaseWrapper getSubscriptionList(Pageable pageable) {
        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());

        Sort sort = Sort.by("modifiedTs").descending();

        if (!hasValue(pageable)) {
            pageable = PageRequest.of(0, 10, sort);
        } else {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        }

        List<ProSubscription> proSubscriptions = proSubscriptionRepository.findByProIdAndActiveStatus(proDetails.getId(), ActiveStatus.ACTIVE.value(), pageable);
        if (proSubscriptions.isEmpty())
            return new BaseWrapper(new ArrayList<>());

        List<SubscriptionListDTO> subscriptionList = new ArrayList<>();
        for (ProSubscription subscription : proSubscriptions)
            subscriptionList.add(new SubscriptionListDTO(subscription));

        Pagination pagination = new Pagination(subscriptionList, subscriptionList.size(), pageable);
        return new BaseWrapper(subscriptionList, pagination);
    }

    @Override
    public BaseWrapper getCurrentSubscription() {

        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());
        log.info("proDetails.getId() = {}", proDetails.getId());
        ProSubscription currentProSubscription = proSubscriptionRepository.findByProIdAndModifiedTs(proDetails.getId(), ActiveStatus.ACTIVE.value());

        List<String> reference = Arrays.asList(ApplicationConstants.SUBSCRIPTION_REFERENCE);

        List<PPLookUp> referenceList = ppLookupRepository.findByActiveStatusAndReferenceIn(ActiveStatus.ACTIVE.value(), reference, Sort.by("modifiedTs"));

        CurrentSubscriptionDTO currentSubscription = new CurrentSubscriptionDTO(proDetails, currentProSubscription, referenceList);

        return new BaseWrapper(currentSubscription);
    }

    @Override
    public BaseWrapper addCurrentSubscription(Long proId, Long subscriptionId, LocalDate subscriptionDate,
                                              TransactionType transactionType, AddPROSubscriptionDTO request) throws IOException, ApiException, PPServicesException, ServicesException, ParseException {
        //Get pro details from proId
        Optional<ProDetails> proDetailsOptional = proDetailsRepository.findById(proId);
        if (!proDetailsOptional.isPresent())
            throw new PPServicesException("608");
        ProDetails proDetails = proDetailsOptional.get();

        //Get all subscription
        List<ProSubscription> newSubscriptionList = proSubscriptionRepository.getSubscriptionList(proId);
//        List<ProSubscription> newSubscriptionList = new ArrayList<>();
//        if (!olderProSubscriptionList.isEmpty()) {
//            for (ProSubscription subscription : olderProSubscriptionList) {
//                subscription.setSubscriptionStatus(SubscriptionStatus.DEACTIVE);
//                subscription.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.INACTIVE.value());
//                newSubscriptionList.add(subscription);
//            }
//        }

        //Get master subscription data
        Optional<MasterSubscription> masterSubscriptionOptional = masterSubscriptionRepository.findById(subscriptionId);
        if (!masterSubscriptionOptional.isPresent())
            throw new PPServicesException("703");
        MasterSubscription masterSubscription = masterSubscriptionOptional.get();

        double subscriptionAmt = masterSubscription.getSubscriptionAmount();

        //Generate transactionId
        int transactionId = generateTransactionId();

        //Get the MakePaymentResponse Wrapper
//        MakePaymentResponseWrapper responseWrapper = paymentService.getMakePaymentResponseWrapper(
//                transactionId, transactionType, dummyCheckoutUrl, enablePaymentPortal,
//                returnUrl, successUrl, failureUrl, masterSubscription.getCurrency().getCode(),
//                masterSubscription.getSubscriptionAmount().longValue());

        PaymentDetails paymentDetails = new PaymentDetails(proDetails, GlobalConstants.PaymentMethods.PAYPAL, PaymentStatus.INITIATED.name(),
                transactionId, transactionType, null, true, GlobalConstants.USER_API,
                ActiveStatus.PAYMENT_INITIATED.value(), null);
        paymentDetailsRepository.save(paymentDetails);

        //add in pro-subscription table
        ProSubscription proSubscription = new ProSubscription();
        proSubscription.setProDetails(proDetails);
        proSubscription.setSubscriptionStatus(SubscriptionStatus.INITIATED);

        //update wallet details and transaction table
        WalletTransactionHistory walletTransactionHistory = null;
        if (masterSubscription != null) {
            //Update wallet transaction history and top up history
            Double proWalletAmount = subscriptionAmt * masterSubscription.getProWalletPercentage() / 100;
            walletTransactionHistory = walletHelper.addProWalletTransactionHistoryAndTopUpHistory(proDetails, paymentDetails, transactionId,
                    proWalletAmount, subscriptionAmt, transactionType, null);
        }

        //update pro_subscription table
        String subscriptionDateYYMMDD = subscriptionDate.toString();
        proSubscription.setDateOfSubscription(subscriptionDateYYMMDD);
        if (masterSubscription != null) {
            proSubscription.setTenure(masterSubscription.getSubscriptionTenure());
            LocalDate renewalDate = subscriptionDate.plusMonths(masterSubscription.getSubscriptionTenure().longValue());
            String expiryDate = renewalDate.toString();
            proSubscription.setSubscriptionExpiresOn(expiryDate);
        }

        proSubscription.setMasterSubscription(masterSubscription);
        proSubscription.setPaymentDetails(paymentDetails);
        proSubscription.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.PAYMENT_INITIATED.value());
        proSubscription.setWalletTransactionHistory(walletTransactionHistory);

        //save All PRO Subscription History
        newSubscriptionList.add(proSubscription);
        proSubscriptionRepository.saveAll(newSubscriptionList);

        //Generate subscription invoice
        SubscriptionInvoice subscriptionInvoice = new SubscriptionInvoice();
        Integer invoiceNo = generateInvoiceNo();
        subscriptionInvoice.setInvoiceNo(invoiceNo);
        subscriptionInvoice.setInvoiceTs(System.currentTimeMillis());
        subscriptionInvoice.setProSubscription(proSubscription);
        subscriptionInvoice.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.PAYMENT_INITIATED.value());
        subscriptionInvoiceRepository.save(subscriptionInvoice);

        //If subscription type is free subscription then activate all the transaction records
        if (masterSubscription.getSubscriptionType() == SubscriptionTypes.FREE_SUBSCRIPTION) {
            PaymentsDetailsResponse paymentsDetailsResponse = new PaymentsDetailsResponse();
            paymentsDetailsResponse.setResultCode(PaymentsResponse.ResultCodeEnum.AUTHORISED);
            paymentsDetailsResponse.setPspReference(Integer.toString(transactionId));

            paymentsRedirectService.doHandlePaymentResult(transactionId, paymentsDetailsResponse, false);
        }

        //If any coupon code is availed then add coupon redemption details in coupon_redemption_details table
        if (hasValue(request) && hasValue(request.getAppliedCouponCode())) {
            CouponCodeDetails couponCodeDetails = couponCodeDetailsRepository.findByCouponCodeIgnoreCaseAndActiveStatus(request.getAppliedCouponCode().trim(), ActiveStatus.ACTIVE.value());
            if (!hasValue(couponCodeDetails))
                throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

            CouponRedemptionDetails couponRedemptionDetails = new CouponRedemptionDetails(couponCodeDetails, proDetails.getUserDetails());
            couponRedemptionDetails.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
            couponRedemptionDetailsRepository.save(couponRedemptionDetails);
        }

        //Create Payment Initiation Response and send
        return new BaseWrapper(paymentHelper.initiatePaymentResponse(transactionId, subscriptionAmt));
    }


    @Override
    public BaseWrapper getSubscriptionList() {
        List<MasterSubscription> masterSubscriptionList = masterSubscriptionRepository.findAllBySubscriptionTypeAndActiveStatus(SubscriptionTypes.NEW_SUBSCRIPTION, ActiveStatus.ACTIVE.value());

        List<String> reference = Arrays.asList(ApplicationConstants.SUBSCRIPTION_REFERENCE);

        List<PPLookUp> referenceList = ppLookupRepository.findByActiveStatusAndReferenceIn(ActiveStatus.ACTIVE.value(), reference, Sort.by("modifiedTs"));

        List<MasterSubscriptionDTO> masterSubscriptionDTOList = new SubscriptionsDTOConverter(referenceList)
                .convert(masterSubscriptionList);
        return new BaseWrapper(masterSubscriptionDTOList);
    }

    @Override
    public MasterSubscriptionDTO getSubscriptionById(Long id) throws ServicesException {
        Optional<MasterSubscription> masterSubscriptionOpt = masterSubscriptionRepository.findById(id);
        if (!masterSubscriptionOpt.isPresent()
                || masterSubscriptionOpt.get().getActiveStatus() != ActiveStatus.ACTIVE.value())
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        MasterSubscription masterSubscription = masterSubscriptionOpt.get();

        List<PPLookUp> referenceList = ppLookupRepository.findByActiveStatusAndReferenceIn(ActiveStatus.ACTIVE.value(), Arrays.asList(masterSubscription.getLookUpTncReference()), Sort.by("modifiedTs"));

        MasterSubscriptionDTO masterSubscriptionDTO = new SubscriptionsDTOConverter(referenceList)
                .convert(masterSubscription);

        return masterSubscriptionDTO;
    }

    @Override
    public void updateSubscriptionTables(Integer txnId, PaymentDetails paymentDetails, PPPaymentResponseType paymentResponse) throws ServicesException {

        boolean isSuccessfulPayment = CommonUtil.isSuccessfulPayment(paymentResponse);

        int activeStatus = isSuccessfulPayment ? ActiveStatus.ACTIVE.value() : ActiveStatus.PAYMENT_FAILED.value();

        // update proSubscription table
        ProSubscription proSubscription = proSubscriptionRepository.getSubscriptionByTXDId(txnId);
        Long proSubIdInTransact = proSubscription.getId();
        SubscriptionStatus proSubscriptionStatus = isSuccessfulPayment ? SubscriptionStatus.ACTIVE : SubscriptionStatus.PENDING;
//        proSubscription.setSubscriptionStatus(proSubscriptionStatus);
//        proSubscription.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
//        System.out.println("proSubscription = " + proSubscription.toString());
//        System.out.println("isSuccessfulPayment - " + isSuccessfulPayment + ", activeStatus - " + activeStatus + ", proSubscriptionStatus - " + proSubscriptionStatus);
//        proSubscriptionRepository.save(proSubscription);
//        System.out.println("proSubscription = " + proSubscription.toString());

        //Get All Subscription Lists
        List<ProSubscription> newSubscriptionList = new ArrayList<>();
        List<ProSubscription> olderProSubscriptionList = proSubscriptionRepository.getSubscriptionList(proSubscription.getProDetails().getId());
        if (!olderProSubscriptionList.isEmpty()) {
            for (ProSubscription subscription : olderProSubscriptionList) {
                log.info("subscription.getId() = {} and proSubIdInTransact = {}", subscription.getId(), proSubIdInTransact);
                if (subscription.getId() == proSubIdInTransact) {
                    proSubscription.setSubscriptionStatus(proSubscriptionStatus);
                    proSubscription.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
                } else {
                    subscription.setSubscriptionStatus(SubscriptionStatus.DEACTIVE);
                    subscription.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.INACTIVE.value());
                }

                newSubscriptionList.add(subscription);
            }
        }

        proSubscriptionRepository.saveAll(newSubscriptionList);

        //Get PRO Details
        ProDetails proDetails = proSubscription.getProDetails();

        //update subscription invoice table
        SubscriptionInvoice subscriptionInvoice = subscriptionInvoiceRepository.getByProSubscriptionId(proSubscription.getId());
        subscriptionInvoice.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
        subscriptionInvoiceRepository.save(subscriptionInvoice);

        //Update Wallet Transaction History table
        WalletTransactionHistory walletTransactionHistory = walletTransactionHistoryRepository.getTransactionHistoryByTXDId(txnId);

        //Update wallet topup History table
        WalletTopUpHistory topUpHistory = walletTopUpHistoryRepository.getTopupHistoryByTxdId(txnId);

        double prajekProCommissionAmount = 0d;
        double subscriptionAmt = 0d;
        double proEssentialAmount = 0d;
        Currency currency = null;
        if (isSuccessfulPayment) {
            //calculate effective amounts out of subscription details
            MasterSubscription masterSubscription = proSubscription.getMasterSubscription();
            subscriptionAmt = masterSubscription.getSubscriptionAmount();

            //Update Pro Wallet Details
            ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(proDetails.getId(), ActiveStatus.ACTIVE.value());
            boolean isCreate = false;
            if (null == proWalletDetails) {
                proWalletDetails = new ProWalletDetails();
                proWalletDetails.setProDetails(proDetails);
                //Set Effective Amount
                setProWalletEffectiveAmt(proWalletDetails, masterSubscription);
                isCreate = true;
            } else if (paymentDetails.getTransactionType() == TransactionType.RENEW_SUBSCRIPTION) {
                //Set Effective Amount
                setProWalletEffectiveAmt(proWalletDetails, masterSubscription);
            }

            proWalletDetails.updateAuditableFields(isCreate, GlobalConstants.USER_API, activeStatus);
            walletDetailsRepository.save(proWalletDetails);

            //Update prajekPro Wallet
            //New Code
            //Calculate Prajekpro Commission Amt
            prajekProCommissionAmount = subscriptionAmt * masterSubscription.getPrajekProWalletPercentage() / 100;
            //Calculate Pro Essential Amount
            proEssentialAmount = subscriptionAmt * masterSubscription.getProEssentialPercentage() / 100;
            //Get Currency to update
            currency = masterSubscription.getCurrency();

            //Old Code
//            List<PrajekProWalletDetails> prajekProWalletDetailsList = prajekproWalletDetailsRepository.getWalletDetailsByTxdId(txdId);
//            List<PrajekProWalletDetails> finalListToSave = new ArrayList<>();
//            for (PrajekProWalletDetails details : prajekProWalletDetailsList) {
//                details.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
//                finalListToSave.add(details);
//            }
//            prajekproWalletDetailsRepository.saveAll(finalListToSave);


            //Update Subscription Status of PRO
            int proStatusOnSuccessfulSubscription = paymentDetails.getTransactionType() == TransactionType.SUBSCRIPTION ? ActiveStatus.APPROVAL_PENDING.value() : ActiveStatus.ACTIVE.value();
            Users users = proDetailsRepository.fetchByUserIdIn(proDetails.getId());
            users.setActiveStatus(proStatusOnSuccessfulSubscription);
            userRepository.save(users);

            //Update wallet transaction history and wallet top up history tables with pro wallet details
            walletTransactionHistory.setProWalletDetails(proWalletDetails);
            topUpHistory.setProWalletDetails(proWalletDetails);
        }

        //Get any existing records by transaction ID
        List<PrajekProWalletDetails> prajekProWalletDetailsList = prajekproWalletDetailsRepository.getWalletDetailsByTxdId(txnId);

        //Update Commision transaction details in Prajekpro wallet
        walletHelper.updatePrajekProWallletDetails(proDetails, prajekProCommissionAmount, WalletAmountType.SUBSCRIPTION_COMISSION,
                currency, walletTransactionHistory, activeStatus, prajekProWalletDetailsList);

        //Update Essential's transaction details in Prajekpro wallet
        walletHelper.updatePrajekProWallletDetails(proDetails, proEssentialAmount, WalletAmountType.ESSENTIAL,
                currency, walletTransactionHistory, activeStatus, prajekProWalletDetailsList);


        //Update Pro Wallet Details in wallet transaction history
        walletTransactionHistory.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
        walletTransactionHistoryRepository.save(walletTransactionHistory);

        //Update Pro Wallet Details in wallet top-up history
        topUpHistory.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
        walletTopUpHistoryRepository.save(topUpHistory);
    }

    private void setProWalletEffectiveAmt(ProWalletDetails proWalletDetails, MasterSubscription masterSubscription) {
        //Calculate Effective Amount
        final double effectiveAmount = (masterSubscription.getSubscriptionAmount() * masterSubscription.getProWalletPercentage() / 100);
        proWalletDetails.setAmount(effectiveAmount);
    }


    @Override
    public void updateSubscriptionFailedTables(Integer txdId, PaymentDetails paymentDetails) {
        // update proSubscription table
        ProSubscription proSubscription = proSubscriptionRepository.getSubscriptionByTXDId(txdId);
        proSubscription.setSubscriptionStatus(SubscriptionStatus.PENDING);
        proSubscription.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.PAYMENT_FAILED.value());
        proSubscriptionRepository.save(proSubscription);


        //update subscription invoice table
        SubscriptionInvoice subscriptionInvoice = subscriptionInvoiceRepository.getByProSubscriptionId(proSubscription.getId());
        subscriptionInvoice.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.PAYMENT_FAILED.value());
        subscriptionInvoiceRepository.save(subscriptionInvoice);

        //update Wallet Transaction History table
        WalletTransactionHistory walletTransactionHistory = walletTransactionHistoryRepository.getTransactionHistoryByTXDId(txdId);
        walletTransactionHistory.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.PAYMENT_FAILED.value());
        walletTransactionHistoryRepository.save(walletTransactionHistory);

        //update wallet topup History table
        WalletTopUpHistory topUpHistory = walletTopUpHistoryRepository.getTopupHistoryByTxdId(txdId);
        topUpHistory.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.PAYMENT_FAILED.value());
        walletTopUpHistoryRepository.save(topUpHistory);

        //update Pro Wallet Details Table
//        ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_Id(paymentDetails.getProDetails().getId());
        ProWalletDetails proWalletDetails = walletTransactionHistory.getProWalletDetails();
        double amount = proWalletDetails.getAmount() - topUpHistory.getAmount();
        proWalletDetails.setAmount(amount);
        proWalletDetails.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.PAYMENT_FAILED.value());
        walletDetailsRepository.save(proWalletDetails);

        //update prajekPro Wallet table
        List<PrajekProWalletDetails> prajekProWalletDetailsList = prajekproWalletDetailsRepository.getWalletDetailsByTxdId(txdId);
        List<PrajekProWalletDetails> finalListToSave = new ArrayList<>();
        for (PrajekProWalletDetails details : prajekProWalletDetailsList) {
            details.updateAuditableFields(false, GlobalConstants.USER_API, ActiveStatus.PAYMENT_FAILED.value());
            finalListToSave.add(details);
        }
        prajekproWalletDetailsRepository.saveAll(finalListToSave);
    }

    @Override
    public BaseWrapper renewSubscription() throws PPServicesException, IOException, ApiException, ServicesException, ParseException {
        final String TAG = "renewSubscription()";

        Users user = authorizationService.fetchLoggedInUser();

        //Get PRO Wallet Details
        ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_UserDetails_UserIdAndActiveStatus(user.getUserId(), ActiveStatus.ACTIVE.value());
        log.info(GlobalConstants.LOG.INFO, TAG, getClass().getName(), proWalletDetails.toString());

        //Check if sufficient wallet amount is available for subscription
        //Step 1: Get minimum wallet balance to maintain (Wallet Threshhold Amount)
        Configuration walletDefaultLimitconfiguration = configurationRepository.findByConfigName(ConfigEnum.WALLET_DEFAULT_LIMIT.name());
        if (!hasValue(walletDefaultLimitconfiguration)) {
            throw new PPServicesException("702");
        }

        //Step 2: Calculate effective wallet balance to compare
        double proWalletAmount = proWalletDetails.getAmount().isNaN() ? 0d : proWalletDetails.getAmount();
        final double effectiveWalletAmount = proWalletAmount - Double.parseDouble(walletDefaultLimitconfiguration.getConfigValue());

        //Step 3: Get Subscription amount for renewal
        MasterSubscription masterSubscription = masterSubscriptionRepository.findBySubscriptionTypeAndActiveStatus(SubscriptionTypes.RENEW_SUBSCRIPTION, ActiveStatus.ACTIVE.value());

        //Step 4: Check if subscription amount is less than calculated effective wallet balance
        if (effectiveWalletAmount < masterSubscription.getSubscriptionAmount())
            throw new ServicesException("622");

        //Renew Subscription
        //Step 1: Add a new subscription as type = RENEW_SUBSCRIPTION
        ProDetails proDetails = proWalletDetails.getProDetails();
        Long proId = proDetails.getId();
        ProSubscription proSubscriptionDetails = proSubscriptionRepository.getSubscriptionByProId(proId, ActiveStatus.ACTIVE.value());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate subscriptionDate = (LocalDate.parse(proSubscriptionDetails.getSubscriptionExpiresOn(), formatter)).plusDays(1);

//        MakePaymentResponseWrapper responseWrapper = addCurrentSubscription(proId, masterSubscription.getId(), subscriptionDate,
//                TransactionType.RENEW_SUBSCRIPTION);
        BaseWrapper responseWrapper = addCurrentSubscription(proId, masterSubscription.getId(), subscriptionDate,
                TransactionType.RENEW_SUBSCRIPTION, null);

        //Step 2: Update the Payment Details for RENEW_SUBSCRIPTION
//        String checkoutUrl = responseWrapper.getCheckOutUrl();
//        Integer txnId = CommonUtility.getTransactionIdFromPrefix(checkoutUrl.substring(checkoutUrl.lastIndexOf("/")));
        InitiatePaymentResponse initiatePaymentResponse = (InitiatePaymentResponse) responseWrapper.getResponse();
        Integer txnId = Integer.parseInt(initiatePaymentResponse.getTransactionId());
        updateTransactionDetailsByPaymentResponse(txnId, PaymentsResponse.ResultCodeEnum.AUTHORISED);

        return new BaseWrapper();
    }

    private void updateTransactionDetailsByPaymentResponse(Integer txnId, PaymentsResponse.ResultCodeEnum resultCode) throws IOException, ServicesException, ParseException {
        PaymentsDetailsResponse paymentsDetailsResponse = PPUtils.getPaymentDetailsResponse(txnId, resultCode);
        paymentsRedirectService.doHandlePaymentResult(txnId, paymentsDetailsResponse, false);
    }

    @Autowired
    private PaymentsRedirectService paymentsRedirectService;

    private int generateInvoiceNo() {
        int generatedApplicationNo = 1;
        if (subscriptionInvoiceRepository.count() > 0) {
            int maxApplicationNo = subscriptionInvoiceRepository.fetchMaxInvoiceNo();
            generatedApplicationNo = maxApplicationNo + 1;
        }
        return generatedApplicationNo;
    }

    private int generateTransactionId() {
        int generatedTransactionId = 1;
        if (paymentDetailsRepository.count() > 0) {
            int maxTransactionId = paymentDetailsRepository.fetchMaxTransactionId() != null ? ((BigInteger) paymentDetailsRepository.fetchMaxTransactionId()).intValue() : 0;
            generatedTransactionId = maxTransactionId + 1;
        }
        return generatedTransactionId;
    }

    private class SubscriptionsDTOConverter extends DTOConverter<MasterSubscription, MasterSubscriptionDTO> {

        private List<PPLookUp> referenceList;

        public SubscriptionsDTOConverter(List<PPLookUp> referenceList) {
            this.referenceList = referenceList;
        }

        @Override
        public MasterSubscriptionDTO convert(MasterSubscription input) {

            return dtoService.createMasterSubscriptionDTO(input, referenceList);
        }
    }
}
