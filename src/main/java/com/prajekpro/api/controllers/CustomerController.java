package com.prajekpro.api.controllers;


import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.dto.CustomerSearchRequestBodyDTO;
import com.prajekpro.api.service.CustomerService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.dto.SingleValue;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = {
                RestUrlConstants.PP_ADMIN_CUSTOMER
        })
@Api(value = "PRO Api Calls across the application for PP")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @ApiOperation(value = "api to get Customer List for Admin panel")
    @PostMapping()
    public BaseWrapper getCustomerList(@RequestBody CustomerSearchRequestBodyDTO request, Pageable pageable) throws ServicesException {
        return customerService.getCustomerList(request, pageable);
    }

    @ApiOperation(value = "API to Active/DeActivate customer")
    @PutMapping(value = {RestUrlConstants.PP_ADMIN_CUSTOMER_DEACTIVATE})
    public BaseWrapper getCustomerDeactivated(@RequestBody SingleValue<Integer> adminActivationStatus, @PathVariable("userId") String userId) throws ServicesException {
        return customerService.getCustomerDeactivated(userId, adminActivationStatus);
    }

    @ApiOperation(value = "APi for customer Appointment History ")
    @GetMapping(value = {RestUrlConstants.PP_ADMIN_CUSTOMER_APPOINTMENTS_HISTORY})
    public BaseWrapper getCustomerAppointmentsHistory(@PathVariable("userId") String userId, Pageable pageable) throws ServicesException {
        return customerService.getCustomerAppointmentsHistory(userId, pageable);
    }

    @ApiOperation(value = "get customer reviews list")
    @GetMapping(value = {RestUrlConstants.PP_ADMIN_CUSTOMER_REVIEWS})
    public BaseWrapper getCustomerReviews(@PathVariable("userId") String userId, Pageable pageable) throws ServicesException {
        return customerService.getCustomerReview(userId, pageable);
    }

    @ApiOperation(value = "APi for Customer Appointment List ")
    @GetMapping(value = {RestUrlConstants.PP_ADMIN_CUSTOMER_APPOINTMENTS})
    public BaseWrapper getCustomerAppointments(@PathVariable("userId") String userId, Pageable pageable) throws ServicesException {
        return customerService.getCustomerAppointments(userId, pageable);
    }

    @ApiOperation(value = "api to get customer details ")
    @GetMapping(value = {RestUrlConstants.PP_ADMIN_CUSTOMER_DETAILS})
    public BaseWrapper getCustomerDetails(@PathVariable("userId") String userId) throws ServicesException {
        return customerService.getCustomerDetails(userId);
    }
}
