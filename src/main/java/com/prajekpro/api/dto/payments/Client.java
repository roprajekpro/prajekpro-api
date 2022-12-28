package com.prajekpro.api.dto.payments;

import java.io.Serializable;

public class Client implements Serializable {

    public String environment;
    public String paypalSdkVersion;
    public String platform;
    public String productName;
    private final static long serialVersionUID = 7328102732109628109L;

}