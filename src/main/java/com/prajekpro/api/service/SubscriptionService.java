package com.prajekpro.api.service;

import com.adyen.service.exception.ApiException;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.PPPaymentResponseType;
import com.prajekpro.api.enums.TransactionType;
import com.prajekpro.api.exception.PPServicesException;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.text.*;
import java.time.LocalDate;

public interface SubscriptionService {
    BaseWrapper getSubscriptionList(Pageable pageable);

    BaseWrapper getCurrentSubscription();

    BaseWrapper addCurrentSubscription(Long proId, Long subscriptionId, LocalDate subscriptionDate, TransactionType transactionType, AddPROSubscriptionDTO request) throws IOException, ApiException, PPServicesException, ServicesException, ParseException;

    BaseWrapper getSubscriptionList();

    MasterSubscriptionDTO getSubscriptionById(Long id) throws ServicesException;

    void updateSubscriptionTables(Integer txdId, PaymentDetails paymentDetails, PPPaymentResponseType paymentResponse) throws ServicesException;

    void updateSubscriptionFailedTables(Integer txdId, PaymentDetails paymentDetails);

    BaseWrapper renewSubscription() throws PPServicesException, IOException, ApiException, ServicesException, ParseException;
}
