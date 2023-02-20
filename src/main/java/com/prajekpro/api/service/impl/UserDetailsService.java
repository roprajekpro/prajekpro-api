package com.prajekpro.api.service.impl;

import com.prajekpro.api.enums.GeneralErrorCodes;
import com.prajekpro.api.enums.Source;
import com.prajekpro.api.util.CommonUtil;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.enums.ActiveStatus;
import com.safalyatech.common.repository.UsersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@Qualifier("userService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {


    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<Integer> activeStatusList = new ArrayList<>();
        activeStatusList.add(ActiveStatus.INACTIVE.value());
        activeStatusList.add(ActiveStatus.REGISTRATION_INITIATED.value());

        LOG.info("username: {}", username);
        String[] userNameSplitString = username.split("@#", username.length());
        Set<String> roleMasterDtlSet = CommonUtil.getRoles(userNameSplitString[1]);
        Users userDetails = usersRepository.fetchByEmailIdOrCntcNoAndRolesInAndActiveStatusNotIn(userNameSplitString[0], userNameSplitString[0], activeStatusList, roleMasterDtlSet);

        if (userDetails == null) {
            LOG.debug("User is null");
            LOG.debug("User " + username + " not found");
            throw new UsernameNotFoundException(GeneralErrorCodes.ERR_INVALID_PASSWORD_SUPPLIED.value());
        }

        //Create a proxy object to save social handle password overriding normal password in case dataSource != APP
//        UserDetailsProxy userDetailsProxy = new UserDetailsProxy(userDetails);

        //Set Google Handle password as password for Spring Security verification
        System.out.println("username sent = " + username);
        if (userNameSplitString[2].equalsIgnoreCase(Source.GOOGLE_HANDLE.name()))
            userDetails.setFinalPassword(userDetails.getGoogleHandlePassword());
        if (userNameSplitString[2].equalsIgnoreCase(Source.FACEBOOK_HANDLE.name()))
            userDetails.setFinalPassword(userDetails.getGoogleHandlePassword());

        return userDetails;
    }

    public UserDetails loadUserByUserID(String userId) throws UsernameNotFoundException {
        Users userDetails = usersRepository.fetchByUserIdAndActiveStatusIn(userId, Arrays.asList(ActiveStatus.ACTIVE.value()));

        if (userDetails == null) {
            LOG.debug("User id is null");
            LOG.debug("User id " + userId + " not found");
            throw new UsernameNotFoundException("User id " + userId + " is not available. Please contact support.");

        }
        return userDetails;

    }

}