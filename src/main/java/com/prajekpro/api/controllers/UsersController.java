package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.dto.ChangePasswordDTO;
import com.prajekpro.api.dto.PPRegisterVO;
import com.prajekpro.api.service.UserService;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.DuplicateRecordException;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = {
        RestUrlConstants.PP_USERS
})
@Api(value = "API's to perform operations specific to user info")
public class UsersController {

    @Autowired
    private UserService userService;

    @PostMapping(value = RestUrlConstants.LOGIN)
    public OAuth2AccessToken loginUser(
            Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        parameters.keySet().forEach(key -> log.info("{} = {}", key, parameters.get(key)));
        return userService.loginUser(principal, parameters);
    }

    private boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return "refresh_token".equals(parameters.get("grant_type")) && parameters.get("refresh_token") != null;
    }

    @PutMapping
    @ApiOperation(value = "Update user profile information")
    public BaseWrapper updateUserProfileInfo(
            @Valid @RequestBody PPRegisterVO request) throws ServicesException, DuplicateRecordException {

        return userService.updateUserProfileInfo(request);

    }

   /* @ApiOperation(value = "Api to upload profile image for a user", response = BaseWrapper.class)
    @PostMapping(value = RestUrlConstants.PP_UPLOAD_PROFILE_IMAGE)
    public BaseWrapper uploadNoticeImage(
            @RequestParam("file") MultipartFile file) throws ServicesException {

        return userService.uploadProfileImage(file);
    }*/

    @ApiOperation(value = "Api to change password for a user", response = BaseWrapper.class)
    @PostMapping(value = RestUrlConstants.PP_CHANGE_PASSWORD)
    public BaseWrapper changePassword(
            @RequestBody ChangePasswordDTO request) throws ServicesException {
        return userService.changePassword(request);
    }

    @ApiOperation(value = "Get customer users information", response = Users.class, responseContainer = "List")
    @GetMapping(value = {RestUrlConstants.PP_CUSTOMER})
    public BaseWrapper getUsers(Pageable pageable) {
        return userService.getCustomerUsers(pageable);
    }

    @ApiOperation(value = "Logout the loggedIn User", response = BaseWrapper.class)
    @PostMapping(value = RestUrlConstants.PP_LOGOUT)
    public BaseWrapper logoutUser(@PathVariable("deviceId") String deviceId) throws ServicesException {
        return userService.logoutUser(deviceId);
    }


}
