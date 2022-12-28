package com.prajekpro.api.service.impl;

import com.adyen.model.checkout.*;
import com.adyen.service.exception.*;
import com.prajekpro.api.domain.PaymentDetails;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.domain.specifications.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
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
import org.springframework.data.jpa.domain.*;
import org.springframework.stereotype.*;

import javax.transaction.*;
import java.io.*;
import java.util.*;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletDetailsRepository walletDetailsRepository;
    @Autowired
    private WalletTransactionHistoryRepository walletTransactionHistoryRepository;
    @Autowired
    private WalletTopUpHistoryRepository walletTopUpHistoryRepository;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private ProDetailsRepository proDetailsRepository;
    @Autowired
    private AppointmentDetailsRepository appointmentDetailsRepository;
    @Autowired
    private PPLookupRepository ppLookupRepository;
    @Autowired
    private SubscriptionInvoiceRepository subscriptionInvoiceRepository;
    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;
    @Autowired
    private PrajekproWalletDetailsRepository prajekproWalletDetailsRepository;
    @Autowired
    private IPaymentService paymentService;

    @Value("${payments.gcash.returnUrl}")
    private String returnUrl;
    @Value("${payments.gcash.dummyCheckoutUrl}")
    private String dummyCheckoutUrl;

    @Autowired
    private PaymentsHelper paymentHelper;


    @Override
    public BaseWrapper getWalletDetailsByProId(Pageable pageable) throws ServicesException {

        String userId = authorizationService.fetchLoggedInUser().getUserId();
//        String userId = "0c7be930-0518-4c9b-8390-a96ec4bbaa77";
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(userId);
        Long proId = proDetails.getId();
        log.debug("proId = {}", proId);

        if (proId == null) {
            throw new ServicesException(GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());
        }
        ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(proId, ActiveStatus.ACTIVE.value());

        if (proWalletDetails == null) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        //TODO:Sorting by CreatedTs directly @Query madhe keli ahe

        List<TransactionType> transactionType = Arrays.asList(TransactionType.APPOINTMENT, TransactionType.LOAD_WALLET, TransactionType.SUBSCRIPTION);
        Page<WalletTransactionHistory> walletTransactionHistoryPage = walletTransactionHistoryRepository.findAllByProWalletDetails_Id(proWalletDetails.getId(), transactionType,
                ActiveStatus.ACTIVE.value(), pageable);
        List<WalletTransactionHistory> walletTransactionHistoryList = walletTransactionHistoryPage.getContent();
        WalletDetailsDTO walletDetailsDTO = new WalletDetailsDTO(proWalletDetails, walletTransactionHistoryList);
        return new BaseWrapper(walletDetailsDTO);
    }

    @Override
    public BaseWrapper getWalletTopUpHistory(Pageable pageable) throws ServicesException {
        String userId = authorizationService.fetchLoggedInUser().getUserId();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(userId);
        Long proId = proDetails.getId();
        log.debug("proId = {}", proId);

        if (proId == null) {
            throw new ServicesException(GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());
        }
        ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(proId, ActiveStatus.ACTIVE.value());

        if (proWalletDetails == null) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        Page<WalletTopUpHistory> walletTopUpHistoryPage = walletTopUpHistoryRepository.findAllByProWalletDetails_Id(proWalletDetails.getId(), ActiveStatus.ACTIVE.value(), pageable);

        List<WalletTopUpHistory> walletTopUpHistoryList = walletTopUpHistoryPage.getContent();
        List<WalletTopUpHistoryDTO> walletTopUpHistoryDTOList = new ArrayList<>();
        for (WalletTopUpHistory topUpHistory : walletTopUpHistoryList) {
            walletTopUpHistoryDTOList.add(new WalletTopUpHistoryDTO(topUpHistory));
        }
        return new BaseWrapper(walletTopUpHistoryDTOList);
    }

    @Override
    public BaseWrapper addWalletAmount(WalletAddAmountDTO request) throws ServicesException, IOException, ApiException {
        String userId = authorizationService.fetchLoggedInUser().getUserId();
        System.out.println("userId = " + userId);
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(userId);
        Long proId = proDetails.getId();
        log.debug("proId = {}", proId);

        if (proId == null)
            throw new ServicesException(GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());

        int transactionId = generateTransactionId();

        ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(proId, ActiveStatus.ACTIVE.value());

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setProDetails(proDetails);
        paymentDetails.setPaymentMethod(GlobalConstants.PaymentMethods.PAYPAL);
        paymentDetails.setPaymentStatus(PaymentStatus.INITIATED.name());
        paymentDetails.setTransactionId(transactionId);
        paymentDetails.setTransactionType(TransactionType.LOAD_WALLET);
        paymentDetails.setRedirectResultCode(PaymentsResponse.ResultCodeEnum.AUTHORISED.name());
        paymentDetails.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.PAYMENT_INITIATED.value());
        paymentDetailsRepository.save(paymentDetails);

        List<String> reference = Arrays.asList("source");
        List<PPLookUp> sourceList = ppLookupRepository.findByActiveStatusAndReferenceIn(ActiveStatus.ACTIVE.value(), reference, Sort.by(Sort.Direction.ASC, Fields.value.name()));
        List<Long> lookupId = new ArrayList<>();
        for (PPLookUp lookup : sourceList) {
            lookupId.add(lookup.getId());
        }
        if (!(lookupId.contains(request.getSourceId()))) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }


        // wallet transaction history table
        double requestAmount = request.getAmount();
        WalletTransactionHistory walletTransactionHistory = new WalletTransactionHistory();
        walletTransactionHistory.setTransactionId(transactionId);
        walletTransactionHistory.setAmount(requestAmount);
        walletTransactionHistory.setProDetails(proDetails);
        walletTransactionHistory.setTransactionType(TransactionType.LOAD_WALLET);
        walletTransactionHistory.setPaymentDetails(paymentDetails);
        walletTransactionHistory.setProWalletDetails(proWalletDetails);
        walletTransactionHistory.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.PAYMENT_INITIATED.value());
        walletTransactionHistoryRepository.save(walletTransactionHistory);

        // wallet top up table
        WalletTopUpHistory topUpHistory = new WalletTopUpHistory(request, proDetails, proWalletDetails);
        topUpHistory.setWalletTransactionHistory(walletTransactionHistory);
        topUpHistory.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.PAYMENT_INITIATED.value());
        walletTopUpHistoryRepository.save(topUpHistory);

        //Prepare Initiate Payment Response and send
        return new BaseWrapper(paymentHelper.initiatePaymentResponse(transactionId, requestAmount));
    }

    @Override
    public BaseWrapper getPrajekProWalletList(PrajekProWalletSearchRequestDTO request, Pageable pageable) {


        log.debug("to fetch prajek pro wallet details");
        Sort sort = Sort.by("createdTs").descending();
        if (pageable.isPaged()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        } else {
            pageable = PageRequest.of(0, 10, sort);
        }

        Page<PrajekProWalletDetails> prajekProWalletDetailsPage = null;
        if (!CheckUtil.hasValue(request)) {
            prajekProWalletDetailsPage = prajekproWalletDetailsRepository.findAll(pageable);
        } else {
            Specification<PrajekProWalletDetails> spec = new SearchPrajekProWalletSpecification(request);
            prajekProWalletDetailsPage = prajekproWalletDetailsRepository.findAll(spec, pageable);
        }

        List<PrajekProWalletDetails> prajekProWalletDetailsList = new ArrayList<>();
        if (prajekProWalletDetailsPage.hasContent()) {
            prajekProWalletDetailsList = prajekProWalletDetailsPage.getContent();
        }

        List<PrajekProWalletResponseWrapper> responseWrappers = new ArrayList<>();
        for (PrajekProWalletDetails walletDetail : prajekProWalletDetailsList) {

            PrajekProWalletResponseWrapper response = new PrajekProWalletResponseWrapper(walletDetail);
            responseWrappers.add(response);
        }

        Pagination page = new Pagination(responseWrappers, prajekProWalletDetailsPage.getTotalElements(), pageable);
        return new BaseWrapper(responseWrappers, page);
    }

    @Override
    public void updateWalletAmountTables(Integer txdId, PaymentDetails paymentDetails, PPPaymentResponseType paymentResponse) {
        boolean isSuccessfulPayment = CommonUtil.isSuccessfulPayment(paymentResponse);

        int activeStatus = isSuccessfulPayment ? ActiveStatus.ACTIVE.value() : ActiveStatus.PAYMENT_FAILED.value();

        //update Wallet Transaction History table
        WalletTransactionHistory walletTransactionHistory = walletTransactionHistoryRepository.getTransactionHistoryByTXDId(txdId);
        walletTransactionHistory.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
        walletTransactionHistoryRepository.save(walletTransactionHistory);

        //update wallet topup History table
        WalletTopUpHistory topUpHistory = walletTopUpHistoryRepository.getTopupHistoryByTxdId(txdId);
        topUpHistory.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
        walletTopUpHistoryRepository.save(topUpHistory);

        if (isSuccessfulPayment) {
            //update Pro Wallet Details Table
            ProWalletDetails proWalletDetails = walletTransactionHistory.getProWalletDetails();
            Double amount = 0d;
            double topUpAmount = topUpHistory.getAmount();
            String proEmailId = proWalletDetails.getProDetails().getUserDetails().getEmailId();
            System.out.println("Logged In User - " + proEmailId);
            if (!CheckUtil.hasValue(proWalletDetails)) {
                log.debug("amount when wallet is empty = {}", topUpAmount);
                amount = topUpAmount;
                proWalletDetails = new ProWalletDetails();
                proWalletDetails.updateAuditableFields(true, proEmailId, activeStatus);
            } else {
                amount = (null == proWalletDetails.getAmount() ? 0 : proWalletDetails.getAmount()) + topUpAmount;
                proWalletDetails.updateAuditableFields(false, proEmailId, activeStatus);
                log.debug("amount when wallet is not empty={}", amount);
            }
            proWalletDetails.setAmount(amount);

            walletDetailsRepository.save(proWalletDetails);
        }
    }

    private int generateTransactionId() {
        int generatedTransactionId = 1;
        if (walletTransactionHistoryRepository.count() > 0) {
            int maxTransactionId =
                    walletTransactionHistoryRepository.fetchMaxTransactionId();
            generatedTransactionId = maxTransactionId + 1;
        }
        return generatedTransactionId;
    }

}
