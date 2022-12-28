package com.prajekpro.api.helpers;

import com.adyen.model.checkout.*;
import com.prajekpro.api.domain.Currency;
import com.prajekpro.api.domain.PaymentDetails;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.enums.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.math.*;
import java.util.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Slf4j
@Component
public class WalletHelper {

    @Autowired
    private WalletDetailsRepository walletDetailsRepository;
    @Autowired
    private PrajekproWalletDetailsRepository prajekproWalletDetailsRepository;
    @Autowired
    private WalletTransactionHistoryRepository walletTransactionHistoryRepository;
    @Autowired
    private WalletTopUpHistoryRepository walletTopUpHistoryRepository;
    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;


    public void releaseAllApptLockedAmtToProWallet(AppointmentDetails appointmentDetails) {
        releaseAllApptLockedAmtToProWalletWithCancellationReimbursement(appointmentDetails, null);
    }

    public void releaseAllApptLockedAmtToProWalletWithCancellationReimbursement(AppointmentDetails appointmentDetails, Double cancellationReimbursementAmt) {
        Double apptTotalLockedAmount = appointmentDetails.getCancellationPnltyLockedAmount() + appointmentDetails.getPrajekProLockedAmount();

        //Get PRO Wallet details
        ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(
                appointmentDetails.getBookedFor().getId(), ActiveStatus.ACTIVE.value());

        Double proWalletAmount = proWalletDetails.getAmount() + apptTotalLockedAmount;
        if (null != cancellationReimbursementAmt)
            proWalletAmount = proWalletAmount + cancellationReimbursementAmt;
        proWalletDetails.setAmount(proWalletAmount);

        //Deduct the relased appt. locked amt from total PRO locked amt
        Double proWalletLockedAmount = proWalletDetails.getLockedAmount() - apptTotalLockedAmount;
        proWalletDetails.setLockedAmount(proWalletLockedAmount);

        walletDetailsRepository.save(proWalletDetails);
    }

    public void releaseApptCommissionAmtToProWallet(AppointmentDetails appointmentDetails) {
        Double apptCommissionAmt = appointmentDetails.getPrajekProLockedAmount();

        //Get PRO Wallet details
        ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(
                appointmentDetails.getBookedFor().getId(), ActiveStatus.ACTIVE.value());
        System.out.println("proWalletDetails Booked For - " + appointmentDetails.getBookedFor().getId() + ", proWallet - " + proWalletDetails);

        //Relase apptCommissionAmt to wallet
        Double proWalletAmount = (null == proWalletDetails.getAmount()) ? apptCommissionAmt : (proWalletDetails.getAmount() + apptCommissionAmt);
        proWalletDetails.setAmount(proWalletAmount);

        //Deduct the relased amount from locked amount
        Double proWalletLockedAmount = proWalletDetails.getLockedAmount() - apptCommissionAmt;
        proWalletDetails.setLockedAmount(proWalletLockedAmount);

        walletDetailsRepository.save(proWalletDetails);

        //Create an active and completed Payment Details record
        int transactionId = generateTransactionId();
        TransactionType transactionType = TransactionType.REFUND;
        ProDetails proDetails = appointmentDetails.getBookedFor();
        PaymentDetails paymentDetails = new PaymentDetails(proDetails, GlobalConstants.PaymentMethods.PAYPAL, PaymentStatus.COMPLETED.name(),
                transactionId, transactionType, PaymentsResponse.ResultCodeEnum.AUTHORISED.name(), true, GlobalConstants.USER_API,
                ActiveStatus.ACTIVE.value(), null);
        paymentDetailsRepository.save(paymentDetails);

        //Create an active transaction record for refund
        WalletTransactionHistory walletTransactionHistory = addProWalletTransactionHistory(appointmentDetails.getBookedFor(), paymentDetails,
                transactionId, apptCommissionAmt, TransactionType.REFUND, appointmentDetails, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());

        //Create an active top up record for refund
        addProTopUpHistory(apptCommissionAmt, transactionType, proDetails, walletTransactionHistory, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());

    }

    public void addPrajekProWalletTransaction(Double amtToAddToWallet, WalletAmountType walletAmtType, int activeStatus,
                                              ProDetails proDetails, AppointmentDetails appointmentDetails,
                                              WalletTransactionHistory walletTransactionHistory) {
        PrajekProWalletDetails prajekProWalletDetails = new PrajekProWalletDetails(proDetails, amtToAddToWallet,
                walletAmtType, getCurrency(appointmentDetails), walletTransactionHistory);
        prajekProWalletDetails.updateAuditableFields(true, GlobalConstants.USER_API, activeStatus);

        prajekproWalletDetailsRepository.save(prajekProWalletDetails);
    }

    private Currency getCurrency(AppointmentDetails appointmentDetails) {
        Currency currency = null;
        for (AppointmentRequestedServices services : appointmentDetails.getAppointmentRequestedServices()) {
            for (AppointmentRequestedServiceCategories category : services.getAppointmentRequestedServiceCategories()) {
                for (AppointmentRequestedServiceSubCategories subcategory : category.getAppointmentRequestedServiceSubCategories()) {
                    currency = subcategory.getServiceItemSubCategory().getCurrency();
                    break;
                }
                if (hasValue(currency)) {
                    break;
                }
            }
            if (hasValue(currency)) {
                break;
            }
        }

        return currency;
    }

    public void updatePrajekProWallletDetails(ProDetails proDetails, Double prajekProWalletAmount,
                                              WalletAmountType walletAmountType, Currency currency,
                                              WalletTransactionHistory walletTransactionHistory,
                                              int walletActiveStatus, List<PrajekProWalletDetails> prajekProWalletDetailsList) {
        boolean isCreate = false;

        //Check for an existing record
        PrajekProWalletDetails prajekProWalletDetails = prajekProWalletDetailsList.stream()
                .filter(pproWallet -> pproWallet.getWalletAmountType() == walletAmountType).findFirst().orElse(null);

        if (!hasValue(prajekProWalletDetails)) {
            prajekProWalletDetails = new PrajekProWalletDetails(proDetails, prajekProWalletAmount, walletAmountType, currency, walletTransactionHistory);
            isCreate = true;
        }
        prajekProWalletDetails.updateAuditableFields(isCreate, GlobalConstants.USER_API, walletActiveStatus);
        prajekproWalletDetailsRepository.save(prajekProWalletDetails);
    }


    public WalletTransactionHistory addProWalletTransactionHistoryAndTopUpHistory(ProDetails proDetails, PaymentDetails paymentDetails, int transactionId,
                                                                                  Double topUpAmount, Double transactionAmount,
                                                                                  TransactionType transactionType, AppointmentDetails appointmentDetails) {
        //update wallet_transaction table
        WalletTransactionHistory walletTransactionHistory = addProWalletTransactionHistory(proDetails, paymentDetails,
                transactionId, transactionAmount, transactionType, appointmentDetails, GlobalConstants.USER_API, ActiveStatus.PAYMENT_INITIATED.value());

        //update wallet_topup table
        addProTopUpHistory(topUpAmount, transactionType, proDetails, walletTransactionHistory, GlobalConstants.USER_API, ActiveStatus.PAYMENT_INITIATED.value());

        return walletTransactionHistory;
    }

    public WalletTopUpHistory addProTopUpHistory(Double topUpAmount, TransactionType transactionType, ProDetails proDetails,
                                                 WalletTransactionHistory walletTransactionHistory, String loggedInUserId, int activeStatus) {
        ProWalletDetails proWalletDetails = walletTransactionHistory.getProWalletDetails();

        WalletTopUpHistory walletTopUpHistory = new WalletTopUpHistory(5L, topUpAmount, proDetails, proWalletDetails);
        walletTopUpHistory.setTransactionType(transactionType);
        walletTopUpHistory.setWalletTransactionHistory(walletTransactionHistory);
        walletTopUpHistory.updateAuditableFields(true, loggedInUserId, activeStatus);

        walletTopUpHistoryRepository.save(walletTopUpHistory);

        return walletTopUpHistory;
    }

    public WalletTransactionHistory addProWalletTransactionHistory(ProDetails proDetails,
                                                                   PaymentDetails paymentDetails, int transactionId,
                                                                   Double transactionAmount, TransactionType transactionType,
                                                                   AppointmentDetails appointmentDetails, String loggedInUserId,
                                                                   int activeStatus) {
        //update wallet details
        ProWalletDetails proWalletDetails = null;
        if (hasValue(proDetails) && hasValue(proDetails.getId()) && proDetails.getId() > 0l) {
            proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(proDetails.getId(), ActiveStatus.ACTIVE.value());
        }

        //update wallet_transaction table
        WalletTransactionHistory walletTransactionHistory = new WalletTransactionHistory();
        walletTransactionHistory.setTransactionId(transactionId);
        walletTransactionHistory.setAmount(transactionAmount);
        walletTransactionHistory.setProDetails(proDetails);
        walletTransactionHistory.setTransactionType(transactionType);
        walletTransactionHistory.setPaymentDetails(paymentDetails);
        walletTransactionHistory.setProWalletDetails(proWalletDetails);
        if (hasValue(appointmentDetails)) {
            walletTransactionHistory.setAppointmentDetails(appointmentDetails);
            walletTransactionHistory.setCustomer(appointmentDetails.getBookedBy());
        }
        walletTransactionHistory.updateAuditableFields(true, loggedInUserId, activeStatus);
        walletTransactionHistoryRepository.save(walletTransactionHistory);

        return walletTransactionHistory;
    }

    public int generateTransactionId() {
        int generatedTransactionId = 1;
        if (paymentDetailsRepository.count() > 0) {
            int maxTransactionId = paymentDetailsRepository.fetchMaxTransactionId() != null ? ((BigInteger) paymentDetailsRepository.fetchMaxTransactionId()).intValue() : 0;
            generatedTransactionId = maxTransactionId + 1;
        }
        return generatedTransactionId;
    }

    public void deductApptCancellationPenaltyAmtFromProLockedAmt(AppointmentDetails appointmentDetails) {
        Double cancellationPnltyAmt = appointmentDetails.getCancellationPnltyLockedAmount();

        //Get PRO Wallet details
        ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(
                appointmentDetails.getBookedFor().getId(), ActiveStatus.ACTIVE.value());

        //Deduct the cancellationPnltyAmt from locked amount
        if (null != proWalletDetails.getLockedAmount()) {
            Double proWalletLockedAmount = proWalletDetails.getLockedAmount() - cancellationPnltyAmt;
            proWalletDetails.setLockedAmount(proWalletLockedAmount);

            walletDetailsRepository.save(proWalletDetails);
        }
    }
}
