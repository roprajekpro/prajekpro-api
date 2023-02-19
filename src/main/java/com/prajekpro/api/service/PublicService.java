package com.prajekpro.api.service;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.itextpdf.text.*;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import org.springframework.core.io.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.*;
import org.springframework.web.multipart.*;

import java.io.*;
import java.util.*;

public interface PublicService {

    BaseWrapper registerCustomer(PPRegisterVO request) throws ServicesException, DuplicateRecordException;

    BaseWrapper forgotPassword(SingleValue<String> request, Source source) throws ServicesException;

    BaseWrapper getCustomerHomerScreenDetails() throws IOException;

    BaseWrapper getProListForAService(Long serviceId, ServiceLocationDTO request, Pageable pageable) throws IOException, ServicesException;

    BaseWrapper getProDetailsForAService(Long serviceId, Long proId) throws JsonParseException, JsonMappingException, IOException, ServicesException;

    OAuth2AccessToken doLogIn(String login, String passw, String source, Source dataSource) throws ServicesException;

    OAuth2AccessToken doLogInPro(Long proId) throws ServicesException;

    BaseWrapper resetPassword(ResetPasswordDTO request) throws ServicesException;

    BaseWrapper sendOtp(SingleValue<String> value, OtpTarget target, Source source) throws ServicesException;

    BaseWrapper verifyOtp(OtpDTO request, String target, Source source) throws ServicesException;

    BaseWrapper registerVendor(PPRegisterVO request) throws ServicesException, DuplicateRecordException;

    BaseWrapper updateProDocuments(Long proId, Set<ProDocuments> request) throws ServicesException;


    BaseWrapper getProDocuments(Long proId) throws ServicesException;

    BaseWrapper getVendorDetails(Long proId) throws ServicesException;


    BaseWrapper uploadProDocuments(Long proId, MultipartFile file) throws ServicesException;

    Object uploadProDocuments(String proId, FileDetailsDTO file) throws ServicesException;

    BaseWrapper addEnquiry(EnquiryDetailsDTO request);

    BaseWrapper verifyUser(SingleValue<String> value, OtpTarget target) throws ServicesException;

    BaseWrapper verifyUserOtp(OtpDTO request, String target) throws ServicesException;

    ResponseEntity<ByteArrayResource> getDownloadImage(String id, DownloadSource source) throws IOException, ServicesException, DocumentException;

    BaseWrapper uploadImage(String id, DownloadSource source, MultipartFile file) throws IOException, ServicesException;

    BaseWrapper getSubscriptionDetails(Long proId) throws ServicesException;

    BaseWrapper registerCustomerViaGoogleHandle(GoogleHandleResponseDTO request) throws DuplicateRecordException, ServicesException;

    BaseWrapper registerCustomerViaFacebookHandle(FacebookLoginDTO request) throws DuplicateRecordException, ServicesException;

    BaseWrapper getCouponDetails(Long proId, String couponCode) throws ServicesException;

    BaseWrapper getStaticContent(StaticContent request) throws ServicesException;

    String generateEncodedPass(String param);
}
