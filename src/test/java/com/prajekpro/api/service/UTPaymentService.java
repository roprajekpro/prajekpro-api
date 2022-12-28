package com.prajekpro.api.service;

import com.adyen.model.checkout.PaymentsDetailsResponse;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.exception.ApiException;
import com.prajekpro.api.PPApiApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest(classes = PPApiApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UTPaymentService {

    private static final String REDIRECT_RESULT = "X6XtfGC3%21eyJyZWRpcmVjdFBheW1lbnREYXRhIjp7ImFjcXVpcmVyQWNjb3VudElkIjozNDM3NjQsImFkZGl0aW9uYWxEYXRhIjp7InJlcXVlc3RBbW91bnQiOiIxMDAiLCJhbnRmaW5hbmNpYWxvc3AudGltZXN0YW1wIjoiIiwicmVxdWVzdEN1cnJlbmN5Q29kZSI6IlBIUCIsInJlcXVlc3RBY3F1aXJlclJlZmVyZW5jZSI6IjA3MDMyNTE2MjUyOTU4MDU3MTQyOTIwMTg4Mzc3NjA3NjY2OGlNR1JOWEdERWkyMDIxMDcwMjAwMDQ3NzM3NDAifSwiYW1vdW50Ijp7ImN1cnJlbmN5IjoiUEhQIiwidmFsdWUiOjEwMH0sImFwaVZlcnNpb24iOjY3LCJicmFuZENvZGUiOiJnY2FzaCIsIm1lcmNoYW50QWNjb3VudCI6IlNhZmFseWFUZWNobm9sb2dpZXNQdnRMdGRFQ09NIiwibWVyY2hhbnRSZWZlcmVuY2UiOiJQSVlVU0gtVEVTVC0xIiwib3JpZ2luYWxQc3BSZWZlcmVuY2UiOiI4ODM2MjUyOTU4MDU3NTVIIiwicmVxdWVzdEFkZGl0aW9uYWxEYXRhIjp7Im1lcmNoYW50SW50ZWdyYXRpb24udHlwZSI6IkNIRUNLT1VUX0dFTkVSSUMiLCJtZXJjaGFudEludGVncmF0aW9uLnZlcnNpb24iOiI2NyJ9LCJyZXR1cm5VcmwiOiJodHRwczpcL1wvY2hlY2tvdXRzaG9wcGVyLXRlc3QuYWR5ZW4uY29tXC9jaGVja291dHNob3BwZXJcL2NoZWNrb3V0UGF5bWVudFJldHVybj9ncGlkPUdQNDBBM0Q1M0FGRDEyOTMzNSIsInNlbGVjdGVkQnJhbmQiOiJnY2FzaCIsInNraW5Db2RlIjoicHViLnYyLjgwMTYyNDgyODQzMDE3NzgueFBiUXJaX2x2eDk0ak9VVWZMZUMxcUd0U1V1Q3VrWW9QYmVNNGROdkxsOCJ9LCJyZXR1cm5VcmxQYXJhbXMiOnsiRFVNTUVZX0tFWSI6IkRVTU1FWV9WQUxVRSJ9fQ%3D%3DtTzomPZd9flqf22OJQRYH%2FT0ACie2KuK2jEjIoehJ7o%3D";
    private static final String ORDER_REF = "PIYUSH-TEST-1";

    @Autowired
    private IPaymentService paymentService;

    @BeforeAll()
    void setUp() {
        assertThat(paymentService).isNotNull();
    }

    @Test
    void getCheckoutUrl_OK() throws IOException, ApiException {

        PaymentsResponse response = paymentService.getCheckoutUrl("PHP", 1000L, ORDER_REF, "https://safalyatech.com/payments/redirectUrl");
        log.info("Checkout Url Request's response = {}", response.toString());
        assertThat(response.getAction()).isNotNull();
        assertThat(response.getAction().getUrl()).isNotNull();
        assertThat(response.getAction().getUrl()).isNotEmpty();
    }

    @Test
    void getPaymentDetailsResponse_OK() throws IOException, ApiException {

        PaymentsDetailsResponse response = paymentService.getPaymentDetails(REDIRECT_RESULT);
        log.info("getPaymentDetailsResponse_OK Response = {}", response.toString());
        assertThat(response).isNotNull();
        assertEquals(ORDER_REF, response.getMerchantReference());
    }
}
