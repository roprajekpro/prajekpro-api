package com.prajekpro.api.service;

import com.adyen.service.exception.ApiException;
import com.prajekpro.api.domain.PaymentDetails;
import com.prajekpro.api.dto.PrajekProWalletSearchRequestDTO;
import com.prajekpro.api.dto.WalletAddAmountDTO;
import com.prajekpro.api.enums.*;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface WalletService {
    BaseWrapper getWalletDetailsByProId(Pageable pageable) throws ServicesException;

    BaseWrapper getWalletTopUpHistory(Pageable pageable) throws ServicesException;

    BaseWrapper addWalletAmount(WalletAddAmountDTO request) throws ServicesException, IOException, ApiException;

    BaseWrapper getPrajekProWalletList(PrajekProWalletSearchRequestDTO request, Pageable pageable);

    void updateWalletAmountTables(Integer txdId, PaymentDetails paymentDetails, PPPaymentResponseType ppPaymentResponseFromResultCode);
}
