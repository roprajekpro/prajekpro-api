package com.prajekpro.api.controllers;

import com.adyen.service.exception.*;
import com.itextpdf.text.*;
import com.prajekpro.api.constants.*;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.exception.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import javax.validation.*;
import java.io.*;
import java.text.*;
import java.time.*;
import java.util.List;
import java.util.*;


@Slf4j
@RestController
@RequestMapping(
        value = {
                RestUrlConstants.PP_PUBLIC
        })
@Api(value = "Public Api Calls across the application for PP")
public class PublicController {

    @Autowired
    private PublicService publicService;
    @Autowired
    private UserService userService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private ProService proService;

    @GetMapping(value = {
            RestUrlConstants.PING
    })
    public BaseWrapper pingTest() {

        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");

        return new BaseWrapper("Service response sent successfully!!!");
    }

    @GetMapping(value = {RestUrlConstants.NOW})
    public BaseWrapper getNow() {
        return new BaseWrapper(new NowDTO(System.currentTimeMillis()));
    }

    @GetMapping(value = {
            "pe/{param}"
    })
    public BaseWrapper generateEncodedPass(@PathVariable("param") String param) {

        return new BaseWrapper(publicService.generateEncodedPass(param));
    }

    @ApiOperation(value = "Api to register a customer using google social handle")
    @PostMapping(value = {
            RestUrlConstants.PP_SOCIAL_HANDLES_GOOGLE_HANDLE
    })
    public BaseWrapper registerCustomerViaGoogleHandle(
            @RequestBody GoogleHandleResponseDTO request) throws ServicesException, DuplicateRecordException {

        log.info("Google Handle Registration request parameters = {}", request.toString());
        publicService.registerCustomerViaGoogleHandle(request);

        return new BaseWrapper(
                publicService.doLogIn(
                        request.getEmail().trim(),
                        request.getUserId().trim(), Source.CUSTOMER_APP.name().toLowerCase(Locale.ROOT), Source.GOOGLE_HANDLE));
    }

    @ApiOperation(value = "Api to register a customer")
    @PostMapping(value = {
            RestUrlConstants.PP_REGISTER
    })
    public BaseWrapper registerCustomer(
            @RequestBody PPRegisterVO request) throws ServicesException, DuplicateRecordException {

        log.info("Registration request parameters = {}", request.toString());
        publicService.registerCustomer(request);

        return new BaseWrapper(
                publicService.doLogIn(
                        request.getEmailId().trim(),
                        request.getPassword().trim(), Source.CUSTOMER_APP.name().toLowerCase(Locale.ROOT), Source.APP));
    }

    @ApiOperation(value = "Api to register a vendor")
    @PostMapping(value = {
            RestUrlConstants.PP_REGISTER_PRO
    })
    public BaseWrapper registerVendor(
            @RequestBody PPRegisterVO request) throws ServicesException, DuplicateRecordException {

        log.info("Registration request parameters = {}", request.toString());
        return publicService.registerVendor(request);
    }

    @ApiOperation(value = "Api to get vendor profile details")
    @GetMapping(value = {
            RestUrlConstants.PP_PRO_DETAILS
    })
    public BaseWrapper getVendorDetails(
            @PathVariable(value = "pro-id") Long proId) throws ServicesException, DuplicateRecordException {

        return publicService.getVendorDetails(proId);
    }

    @ApiOperation(value = "Api to update vendor documents")
    @PostMapping(value = {
            RestUrlConstants.PP_REGISTER_PRO_DOC
    })
    public BaseWrapper updateVendorDocuments(
            @PathVariable(value = "pro-id") Long proId,
            @RequestBody Set<ProDocuments> request) throws ServicesException {

        log.info("Registration request parameters = {}", request.toString());
        return publicService.updateProDocuments(proId, request);
    }

    @ApiOperation(value = "Api to upload vendor documents")
    @PostMapping(value = {
            RestUrlConstants.PP_REGISTER_PRO_DOC_UPLOAD
    })
    public BaseWrapper uploadVendorDocuments(
            @PathVariable(value = "pro-id") Long proId,
            @RequestParam("file") MultipartFile file) throws ServicesException {

//        log.info("Registration request parameters = {}", request.toString());
        return publicService.uploadProDocuments(proId, file);
    }

    @ApiOperation(value = "Api to get vendor documents")
    @GetMapping(value = {
            RestUrlConstants.PP_REGISTER_PRO_DOC
    })
    public BaseWrapper getVendorDocuments(
            @PathVariable(value = "pro-id") Long proId) throws ServicesException {

        return publicService.getProDocuments(proId);
    }

    @ApiOperation(value = "Api for forgot password")
    @PostMapping(value = {
            RestUrlConstants.PP_FORGOT_PASSWORD
    })
    public BaseWrapper forgotPassword(
            @RequestBody SingleValue<String> request, @RequestParam Source source) throws ServicesException {

        return publicService.forgotPassword(request, source);
    }

    @ApiOperation(value = "Api to set password")
    @PostMapping(value = {
            RestUrlConstants.PP_RESET_PASSWORD
    })
    public BaseWrapper resetPassword(
            @RequestBody ResetPasswordDTO request) throws ServicesException {

        return publicService.resetPassword(request);
    }

    @ApiOperation(value = "Api to get home screen details of customer mobile app based on logged in user")
    @GetMapping(value = {
            RestUrlConstants.PP_CUSTOMER_HOME_DETAILS
    })
    public BaseWrapper getCustomerHomeScreenDetails() throws IOException {

        return publicService.getCustomerHomerScreenDetails();
    }

    @ApiOperation(value = "Api to get PRO listing for a service of customer mobile app based on logged in user")
    @PostMapping(value = {
            RestUrlConstants.PP_SERVICE_PRO_LIST
    })
    public BaseWrapper getProListForAService(
            @PathVariable(value = "service-id") Long serviceId,
            @RequestBody ServiceLocationDTO request,
            Pageable pageable) throws IOException, ServicesException {

        return publicService.getProListForAService(serviceId, request, pageable);
    }

    @ApiOperation(value = "Api to get PRO Details for a service of customer mobile app based on logged in user")
    @GetMapping(value = {
            RestUrlConstants.PP_SERVICE_PRO_DETAILS
    })
    public BaseWrapper getProDetailsForAService(
            @PathVariable(value = "service-id") Long serviceId,
            @PathVariable(value = "pro-id") Long proId) throws IOException, ServicesException {

        return publicService.getProDetailsForAService(serviceId, proId);
    }

    @ApiOperation(value = "Api to send OTP")
    @PostMapping(value = {
            RestUrlConstants.PP_SEND_OTP
    })
    public BaseWrapper sendOtp(
            @ApiParam(value = "value will be either emailID or contactNo") @RequestBody SingleValue<String> value,
            @RequestParam(GlobalConstants.OTP_TARGET) OtpTarget target, @RequestParam Source source) throws ServicesException {

        return publicService.sendOtp(value, target, source);
    }

    @ApiOperation(value = "Api to send OTP")
    @PostMapping(value = {
            RestUrlConstants.PP_VERIFY_USER
    })
    public BaseWrapper verifyUser(
            @ApiParam(value = "value will be either emailID or contactNo") @RequestBody SingleValue<String> value,
            @RequestParam(GlobalConstants.OTP_TARGET) OtpTarget target) throws ServicesException {

        return publicService.verifyUser(value, target);
    }


    @PostMapping(value = {
            RestUrlConstants.PP_VERIFY_OTP
    })
    public BaseWrapper verifyOtp(
            @RequestBody OtpDTO request,
            @RequestParam(GlobalConstants.OTP_TARGET
            ) String target, @RequestParam Source source
    ) throws ServicesException {

        return publicService.verifyOtp(request, target, source);
    }

    @PostMapping(value = {
            RestUrlConstants.PP_VERIFY_USER_OTP
    })
    public BaseWrapper verifyUserOtp(
            @RequestBody OtpDTO request,
            @RequestParam(GlobalConstants.OTP_TARGET) String target) throws ServicesException {

        return publicService.verifyUserOtp(request, target);
    }

   /* @ApiOperation(value = "Api to get profile image for a user", response = Byte.class)
    @GetMapping(value = RestUrlConstants.PP_DOWNLOAD_PROFILE_IMAGE)
    public ResponseEntity<ByteArrayResource> downloadUserProfileImage(
            @PathVariable("user-id") String userId) throws ServicesException {

        return userService.downloadUserProfileImage(userId);
    }*/

    @ApiOperation(value = "Api to upload profile image for a vendor", response = BaseWrapper.class)
    @PostMapping(value = RestUrlConstants.PP_REGISTER_PRO_PROFILE)
    public BaseWrapper uploadProProfileImage(
            @PathVariable("pro-id") Long proId,
            @RequestParam("file") MultipartFile file) throws ServicesException {
        return userService.uploadProProfileImage(proId, file);
    }

   /* @ApiOperation(value = "Api to get profile image for a vendor", response = Byte.class)
    @GetMapping(value = RestUrlConstants.PP_DOWNLOAD_PRO_PROFILE_IMAGE)
    public ResponseEntity<ByteArrayResource> downloadProProfileImage(
            @PathVariable("pro-id") Long proId) throws ServicesException {

        return userService.downloadProProfileImage(proId);
    }*/

    @ApiOperation(value = "Api to update vendor location details")
    @PutMapping(value = RestUrlConstants.PP_PRO_LOCATION)
    public BaseWrapper updateProLocation(
            @PathVariable("pro-id") Long proId,
            @RequestBody LocationDetailsDTO request) throws ServicesException {

        return locationService.updateUserLocation(proId, request);
    }

    @ApiOperation(value = "Api to get vendor location details")
    @GetMapping(value = RestUrlConstants.PP_PRO_LOCATION)
    public BaseWrapper getProLocation(
            @PathVariable("pro-id") Long proId) throws ServicesException {

        return locationService.getUserLocationDetails(proId);
    }


    @ApiOperation(value = "API to add contact details from anywhere")
    @PostMapping(value = {RestUrlConstants.PP_PUBLIC_ADD_ENQUIRY_DETAILS})
    public BaseWrapper addEnquiry(@Valid @RequestBody EnquiryDetailsDTO request) {
        return publicService.addEnquiry(request);
    }

    @ApiOperation(value = "Api to get look up details")
    @GetMapping(value = RestUrlConstants.PP_LOOK_UP)
    public BaseWrapper getLookUp(
            @RequestParam(value = "reference", required = true) String reference,
            @RequestParam(value = "term", required = false) String term
    ) throws Exception {

        return locationService.getLookUpList(reference, term);
    }

    @ApiOperation(value = "Api to get list of Subscription")
    @GetMapping(value = RestUrlConstants.PP_SUBSCRIPTION_MASTER)
    public BaseWrapper getSubscription() {

        return subscriptionService.getSubscriptionList();
    }

    @ApiOperation(value = "Api to add current Subscription of pro")
    @PostMapping(value = {RestUrlConstants.PP_ADD_SUBSCRIPTION})
    public BaseWrapper addCurrentSubscription(@PathVariable(value = "pro-id") Long proId,
                                              @PathVariable(value = "subscription-id") Long subscriptionId,
                                              @RequestBody AddPROSubscriptionDTO request) throws ServicesException, IOException, ApiException, PPServicesException, ParseException {

        return subscriptionService.addCurrentSubscription(proId, subscriptionId, LocalDate.now(ZoneOffset.UTC), TransactionType.SUBSCRIPTION, request);
    }

    @ApiOperation(value = "API to get subscription details")
    @GetMapping(value = {RestUrlConstants.PP_PRO_SUBSCRIPTION})
    public BaseWrapper getSubscriptionByProId(@PathVariable("proId") Long proId) throws ServicesException {
        return publicService.getSubscriptionDetails(proId);
    }

    @ApiOperation(value = "Common API to download Image")
    @GetMapping(value = {RestUrlConstants.PP_DOWNLOAD_IMAGE})
    public ResponseEntity<ByteArrayResource> downloadImage(
            @PathVariable("id") String id,
            @RequestParam(value = "source", required = false) DownloadSource source) throws ServicesException, IOException, DocumentException {
        return publicService.getDownloadImage(id, source);
    }

    @ApiOperation(value = "Common API to upload image and documents")
    @PostMapping(value = {RestUrlConstants.PP_UPLOAD_IMAGE_DOCUMENTS})
    public BaseWrapper uploadProfileImageAndDocuments(@PathVariable(value = "id") String id,
                                                      @RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "source", required = false) DownloadSource source) throws IOException, ServicesException {
        return publicService.uploadImage(id, source, file);
    }

    @GetMapping(value = {RestUrlConstants.PP_COUPON_DETAILS})
    @ApiOperation(value = "Api to get coupon code details")
    public BaseWrapper getCouponDetails(
            @PathVariable(value = "proId") Long proId,
            @PathVariable(value = "couponCode") String couponCode) throws ServicesException {
        return publicService.getCouponDetails(proId, couponCode);
    }

    @PostMapping(value = {RestUrlConstants.PP_STATIC_CONTENT})
    @ApiOperation(value = "Api to get static content by CONTENT_ID")
    public BaseWrapper getStaticContent(@RequestBody StaticContent request) throws ServicesException {
        return publicService.getStaticContent(request);
    }

    @Autowired
    public ProServicesService proServicesService;

    @PostMapping("/upload/pro/{proId}/services/{serviceId}/documents")
    @ApiOperation(value = "API to upload Pro Service Documents")
    public BaseWrapper uploadProServiceDocument(
            @PathVariable(value = "proId") Long proId,
            @PathVariable(value = "serviceId") Long serviceId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "docType", defaultValue = DocType.DEFAULT_VALUE) DocType docType
    ) throws ServicesException, IOException {
        return proServicesService.uploadProServiceDocument(proId, serviceId, docType, file);
    }

    @DeleteMapping("/upload/pro/{proId}/services/{serviceId}/documents/{docId}")
    @ApiOperation(value = "API to upload Pro Service Documents")
    public BaseWrapper deleteUploadProServiceDocument(
            @PathVariable(value = "proId") Long proId,
            @PathVariable(value = "serviceId") Long serviceId,
            @PathVariable(value = "docId") Long docId
    ) throws ServicesException, IOException {
        return proServicesService.deleteUploadProServiceDocument(proId, serviceId, docId);
    }

    @ApiOperation(value = "Api to update vendor services")
    @PostMapping(value = {
            RestUrlConstants.PP_REGISTER_PRO_SERVICE
    })
    public BaseWrapper updateVendorServices(
            @PathVariable(value = "pro-id") Long proId,
            @RequestBody List<Services> request) throws ServicesException {

        log.info("Registration request parameters = {}", request.toString());
        proServicesService.updateProServices(proId, request);

        return new BaseWrapper(proService.getProServices(proId, 0l));
    }


    @ApiOperation(value = "Api to get PRO services")
    @GetMapping(value = "/pro/{pro-id}/services")
    public BaseWrapper getAllProServices(
            @PathVariable(value = "pro-id") Long proId) throws ServicesException {

        return new BaseWrapper(proService.getProServices(proId, 0l));
    }


    @ApiOperation(value = "Api to get PRO services")
    @DeleteMapping(value = "/pro/{pro-id}/services/{service-id}")
    public BaseWrapper deleteProServices(
            @PathVariable(value = "pro-id") Long proId,
            @PathVariable(value = "service-id") Long serviceId
    ) throws ServicesException {

        return new BaseWrapper(proServicesService.deleteProServices(proId, serviceId));
    }

    @ApiOperation(value = "Api to register a customer using facebook social handle")
    @PostMapping(value = {
            RestUrlConstants.PP_SOCIAL_HANDLES_FACEBOOK_HANDLE
    })
    public BaseWrapper registerCustomerViaFacebook(
            @RequestBody FacebookLoginDTO request) throws ServicesException, DuplicateRecordException {

        log.info("facebook Handle Registration request parameters = {}", request.toString());
        publicService.registerCustomerViaFacebookHandle(request);

        return new BaseWrapper(
                publicService.doLogIn(
                        request.getEmail().trim(),
                        request.getId().trim(), Source.CUSTOMER_APP.name().toLowerCase(Locale.ROOT), Source.FACEBOOK_HANDLE));
    }
}
