package com.prajekpro.api.service.impl;

import com.prajekpro.api.service.AuthorizationService;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.utility.CheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public Users fetchLoggedInUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (CheckUtil.hasValue(authentication))
            return (Users) authentication.getPrincipal();
        else
            return null;
    }

}
