package com.prajekpro.api.service;

import com.safalyatech.common.exception.ServicesException;

public interface MessagingService {

    boolean sendMessage(String contactNumber, Object message) throws Exception;

    boolean sendOTP(String otp, String mobileNo) throws ServicesException;
}
