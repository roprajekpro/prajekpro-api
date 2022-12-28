package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.service.CommonService;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = {
                RestUrlConstants.PP_COMMON
        })
@Api(value = "Common Api Calls across the application for PP")
public class CommonController {

    @Autowired
    private CommonService commonService;


    @ApiOperation(
            value = "Get logged in user information", response = Users.class)
    @GetMapping(value = {
            RestUrlConstants.PP_USER_INFO
    })
    public BaseWrapper getUsers() {

        return commonService.getUsers();
    }

    @ApiOperation(value = "api to display dashboard information about Appointments")
    @GetMapping(value = {RestUrlConstants.PP_DASHBOARD})
    public BaseWrapper getDashboardInfo() throws ServicesException {
        return commonService.getDashboardInfo();
    }


}
