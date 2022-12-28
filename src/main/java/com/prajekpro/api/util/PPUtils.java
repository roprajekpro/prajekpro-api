package com.prajekpro.api.util;

import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.safalyatech.common.utility.CommonUtility;

public final class PPUtils {

    private PPUtils() {
    }

    public static PaymentsDetailsResponse getPaymentDetailsResponse(Integer txnId, PaymentsResponse.ResultCodeEnum resultCode) {
        PaymentsDetailsResponse paymentsDetailsResponse = new PaymentsDetailsResponse();
        paymentsDetailsResponse.setPspReference(CommonUtility.convertTrxIdIntoString(txnId));
        paymentsDetailsResponse.setResultCode(resultCode);

        return paymentsDetailsResponse;
    }
}
