package com.prajekpro.api.controllers;

import com.adyen.service.exception.ApiException;
import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.exception.PPServicesException;
import com.prajekpro.api.service.SubscriptionService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.*;

@RestController
@RequestMapping(value = {RestUrlConstants.PP_SUBSCRIPTION})
@Api(value = "API related to Subscription ")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @ApiOperation(value = "Api to get list of Subscription by proId")
    @GetMapping()
    public BaseWrapper getListOfSubscription(Pageable pageable) {
        return subscriptionService.getSubscriptionList(pageable);
    }

    @ApiOperation(value = "Api related current Subscription")
    @GetMapping(value = {RestUrlConstants.PP_SUBSCRIPTION_CURRENT})
    public BaseWrapper getCurrentSubscription() {
        return subscriptionService.getCurrentSubscription();
    }

    @ApiOperation(value = "Renew Subscription")
    @GetMapping(value = {RestUrlConstants.PP_SUBSCRIPTION_RENEW})
    public BaseWrapper renewCurrentSubscription() throws PPServicesException, IOException, ApiException, ServicesException, ParseException {
        return subscriptionService.renewSubscription();
    }
}
