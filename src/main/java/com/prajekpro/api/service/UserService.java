package com.prajekpro.api.service;

import com.prajekpro.api.dto.ChangePasswordDTO;
import com.prajekpro.api.dto.PPRegisterVO;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.dto.DownloadImageDTO;
import com.safalyatech.common.dto.FileDetailsDTO;
import com.safalyatech.common.exception.DuplicateRecordException;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

public interface UserService {

    BaseWrapper updateUserProfileInfo(PPRegisterVO request) throws ServicesException, DuplicateRecordException;

    BaseWrapper uploadProfileImage(String id, FileDetailsDTO file) throws ServicesException;

    DownloadImageDTO downloadUserProfileImage(String userId) throws ServicesException;

    BaseWrapper changePassword(ChangePasswordDTO request) throws ServicesException;

    BaseWrapper getCustomerUsers(Pageable pageable);

    @Deprecated
    BaseWrapper uploadProfileImage(MultipartFile file) throws ServicesException;

    @Deprecated
    BaseWrapper uploadProProfileImage(Long proId, MultipartFile file) throws ServicesException;

    BaseWrapper uploadProProfileImage(Long proId, FileDetailsDTO file) throws ServicesException;

    DownloadImageDTO downloadProProfileImage(Long proId) throws ServicesException;

    BaseWrapper logoutUser(String deviceId);

    DownloadImageDTO downloadProDocuments(String id);

    OAuth2AccessToken loginUser(Principal principal, Map<String, String> parameters) throws HttpRequestMethodNotSupportedException;


}
