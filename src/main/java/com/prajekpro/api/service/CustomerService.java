package com.prajekpro.api.service;

import com.prajekpro.api.dto.CustomerSearchRequestBodyDTO;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.dto.SingleValue;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    BaseWrapper getCustomerList(CustomerSearchRequestBodyDTO request, Pageable pageable) throws ServicesException;

    BaseWrapper getCustomerDeactivated(String userId, SingleValue<Integer> isAdminDeactivated) throws ServicesException;

    BaseWrapper getCustomerAppointmentsHistory(String userId, Pageable pageable) throws ServicesException;

    BaseWrapper getCustomerReview(String userId, Pageable pageable) throws ServicesException;

    BaseWrapper getCustomerAppointments(String userId, Pageable pageable) throws ServicesException;

    BaseWrapper getCustomerDetails(String userId) throws ServicesException;
}
