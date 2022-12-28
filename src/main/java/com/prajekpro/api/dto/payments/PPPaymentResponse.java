package com.prajekpro.api.dto.payments;

import java.io.Serializable;

public class PPPaymentResponse implements Serializable {

    public String createTime;
    public String id;
    public String intent;
    public String state;
    private final static long serialVersionUID = -3324980658760253832L;

}