package com.prajekpro.api.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.converters.DTOConverter;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.prajekpro.api.util.EncryptionUtils;
import com.safalyatech.common.constants.GlobalConstants;
import com.safalyatech.common.domains.EnquiryDetails;
import com.safalyatech.common.domains.UserRoleDtl;
import com.safalyatech.common.domains.UserRolePK;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.ActiveStatus;
import com.safalyatech.common.enums.DownloadSource;
import com.safalyatech.common.enums.RegistrationSource;
import com.safalyatech.common.enums.Roles;
import com.safalyatech.common.exception.DuplicateRecordException;
import com.safalyatech.common.exception.ServicesException;
import com.safalyatech.common.repository.EnquiryDetailsRepository;
import com.safalyatech.common.repository.UserRoleDtlRepository;
import com.safalyatech.common.repository.UsersRepository;
import com.safalyatech.common.utility.CheckUtil;
import com.safalyatech.common.utility.CommonUtility;
import com.safalyatech.common.utility.PDFUtils;
import com.safalyatech.common.utility.Pagination;
import com.safalyatech.emailUtility.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.safalyatech.common.constants.StringConstants.PP_DEFAULT_IMAGE;
import static com.safalyatech.common.utility.CheckUtil.hasValue;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class PublicServiceImpl implements PublicService {

    @Autowired
    private CommonUtility commonUtility;

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private DTOFactory dtoService;
    @Autowired
    private MessagingService messagingService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ProDetailsRepository proDetailsRepository;
    @Autowired
    private UserRoleDtlRepository userRoleDtlRepository;
    @Autowired
    private AdvertisementsRepository advertisementsRepository;
    @Autowired
    private CommercialAdvertisementRepository commercialAdvertisementRepository;
    @Autowired
    private UserOtpRepository userOtpRepository;
    @Autowired
    private ProDetailDocumentRepository proDetailDocumentRepository;
    @Autowired
    private StaticContentRepository staticContentRepository;

    @Autowired

    private PasswordEncoder passwordEncoder;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private Environment env;
    @Autowired
    private MasterService masterService;
    @Autowired

    private UserService userService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private ProServicesService proServicesService;

    @Autowired
    private ProCancellationTimeRepository proCancellationTimeRepository;
    @Autowired
    private EnquiryDetailsRepository enquiryDetailsRepository;
    @Autowired
    private ProServiceItemsPricingRepository proServiceItemsPricingRepository;
    @Autowired
    private ProSubscriptionRepository proSubscriptionRepository;
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private CouponCodeDetailsRepository couponCodeDetailsRepository;
    @Autowired
    private CouponRedemptionDetailsRepository couponRedemptionDetailsRepository;
    @Autowired
    private ProServicesRepository proServicesRepository;

    @Value("${file.upload.path}")
    private String fileUploadPath;

    @Value("${otp.activate}")
    private boolean otpActive;
    @Value(("${dummy.pdf.path}"))
    private String dummyPdfPath;
    @Value("${apk.download.path}")
    private String apkDownloadPath;

    @Autowired
    private ChatService chatServiceImpl;


    @Override
    public BaseWrapper registerCustomerViaGoogleHandle(GoogleHandleResponseDTO request) throws DuplicateRecordException, ServicesException {
        PPRegisterVO ppRegisterVO = new PPRegisterVO(request);
        registerUser(ppRegisterVO, Roles.ROLE_CUSTOMER.name(), Source.GOOGLE_HANDLE);

        return new BaseWrapper();
    }

    @Override
    public BaseWrapper registerCustomerViaFacebookHandle(FacebookLoginDTO request) throws DuplicateRecordException, ServicesException {
        PPRegisterVO ppRegisterVO = new PPRegisterVO(request);
        registerUser(ppRegisterVO, Roles.ROLE_CUSTOMER.name(), Source.FACEBOOK_HANDLE);
        return new BaseWrapper();
    }


    @Override
    public BaseWrapper registerCustomer(PPRegisterVO request) throws ServicesException, DuplicateRecordException {
        registerUser(request, Roles.ROLE_CUSTOMER.name(), Source.APP);
        return new BaseWrapper();
    }

    @Override
    public BaseWrapper registerVendor(PPRegisterVO request) throws ServicesException, DuplicateRecordException {
        Users users = registerUser(request, Roles.ROLE_VENDOR.name(), Source.APP);

        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(users.getUserId());
        boolean isCreate = false;
        if (null == proDetails) {
            proDetails = new ProDetails();
            proDetails.setUserDetails(users);
            proDetails.setApplicationNo(generateProApplicationNo());
            proDetails.setAvailabilityStatus(AvailabilityStatus.AVAILABLE.value());
            isCreate = true;
        }

        log.info("request.isVatRegistered() = {}, tncAccepted = {}", request.isVatRegistered(), request.isTncAccepted());
        proDetails.setAboutText(request.getAboutText());
        proDetails.setVatRegistered(request.isVatRegistered());
        proDetails.setVatNo(request.isVatRegistered() ? request.getVatNo() : "N/A");
        proDetails.setExperienceInYears(request.getExperienceInYears());
        proDetails.updateAuditableFields(isCreate, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());

        proDetailsRepository.save(proDetails);

        return new BaseWrapper(proDetails.getId());
    }

    private int generateProApplicationNo() {
        int generatedApplicationNo = 1;
        if (!proDetailsRepository.findAll().isEmpty()) {
            int maxApplicationNo = proDetailsRepository.fetchMaxApplicationNo();
            generatedApplicationNo = maxApplicationNo + 1;
        }
        return generatedApplicationNo;
    }

    @Override
    public BaseWrapper getVendorDetails(Long proId) throws ServicesException {

        Users users = proDetailsRepository.fetchByUserIdIn(proId);
        ProDetails proDetails = getProDetailsFromOptional(proId);
        PPRegisterVO registerVO = new PPRegisterVO();
        registerVO.setFirstName(users.getFirstNm());
        registerVO.setLastName(users.getLastNm());
        registerVO.setEmailId(users.getEmailId());
        registerVO.setContactNo(users.getCntcNo());
        registerVO.setLandlineNo(users.getLandLineNo());

        return new BaseWrapper(registerVO);
    }

    private Users registerUser(PPRegisterVO request, String userRole, Source source) throws DuplicateRecordException, ServicesException {

        String handleEncodedPwd= new String();
        int sourceValue = 0;
        String contactNo = request.getContactNo();
        String emailID = request.getEmailId();

        List<String> roleMasterDtlList = Arrays.asList(userRole);
        List<Users> userList = usersRepository.fetchByEmailIdOrCntcNoAndRolesIn
                (emailID, contactNo, new HashSet<>(roleMasterDtlList), null);

        Users existingUser = null;
        if (hasValue(userList)) {
            if (userList.size() > 1) {
                //Multiple users already exists with same contactNo/email and same Role
                throw new ServicesException(GeneralErrorCodes.ERR_USER_ALREADY_REGISTERED.value());
            }


            existingUser = userList.get(0);
            if (!existingUser.getActiveStatus().equals(ActiveStatus.REGISTRATION_INITIATED.value()) && !userRole.equals(Roles.ROLE_CUSTOMER.name()))
                //On trying to register with same user, whose registration is not in progress
                throw new ServicesException(GeneralErrorCodes.ERR_USER_ALREADY_REGISTERED.value());

            existingUser.setFirstNm(request.getFirstName());
            existingUser.setLastNm(request.getLastName());
            if (source == Source.APP) {
                existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
                existingUser.setSource(RegistrationSource.APP.value());
                sourceValue = RegistrationSource.APP.value();

            }
            if (source == Source.GOOGLE_HANDLE) {
                existingUser.setGoogleHandlePassword(passwordEncoder.encode(request.getGoogleHandlePassword()));
                existingUser.setSource(RegistrationSource.GOOGLE.value());
                handleEncodedPwd = passwordEncoder.encode(request.getGoogleHandlePassword());
                sourceValue = RegistrationSource.GOOGLE.value();
            }
            if(source == Source.FACEBOOK_HANDLE){
                existingUser.setGoogleHandlePassword(passwordEncoder.encode(request.getFacebookHandlePassword()));
                existingUser.setSource(RegistrationSource.GOOGLE.value());
                handleEncodedPwd = passwordEncoder.encode(request.getFacebookHandlePassword());
                sourceValue = RegistrationSource.FACEBOOK.value();
            }
            if(request.getPassword()!=null)
            existingUser.setAccessCode(EncryptionUtils.encrypt(request.getPassword()));
            existingUser.setEmailId(request.getEmailId());
            existingUser.setCntcNo(request.getContactNo());
            existingUser.setLandLineNo(request.getLandlineNo());

            existingUser.setIsContactVerified(request.isContactVerified());
            existingUser.setTncAccepted(request.isTncAccepted());
        }

        String firstName = request.getFirstName();
        String lastName = request.getLastName();
        String encryptedPwd = request.getPassword()!=null ? EncryptionUtils.encrypt(request.getPassword()) :null;
        String appEncodedPwd = source == Source.APP ? passwordEncoder.encode(request.getPassword()) : null;


        boolean isTncAccepted = request.isTncAccepted();
        boolean contactVerified = request.isContactVerified();

        log.debug("Contact number= {}", contactNo);
        log.debug("contactVerified = {}", contactVerified);

        if (hasValue(contactNo)
                && CheckUtil.isValidPhoneNumber(contactNo)
                && hasValue(emailID)
                && CheckUtil.isValidEmail(emailID)
                && hasValue(firstName)
                && hasValue(isTncAccepted)) {

            if (userRole.equalsIgnoreCase(Roles.ROLE_VENDOR.name())) {
                if (!hasValue(contactVerified)) {
                    throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_FOR_REGISTRATION.value());
                }
                if (!contactVerified) {
                    throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
                }
            }


            //TODO: we need to change if condition for proper error message
            if (!isTncAccepted) {
                throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
            }

            if (null != existingUser) {
                //if rqequest is validated and existing user exists, than update
                log.debug("Saving user details with data = {}", existingUser.toString());
                usersRepository.save(existingUser);

                return existingUser;
            }

            //Save user
            String uuid = UUID.randomUUID().toString();

            // TODO: 20-05-2021 Save encrypted password in accessCode
            Users userToRegister = new Users(
                    uuid,
                    firstName,
                    null,
                    lastName,
                    appEncodedPwd,
                    handleEncodedPwd,
                    encryptedPwd,
                    emailID,
                    contactNo,
                    request.getLandlineNo(),
                    sourceValue,
                    userRole.equals(Roles.ROLE_VENDOR.name()) ? ActiveStatus.REGISTRATION_INITIATED.value() : ActiveStatus.ACTIVE.value(),
                    0,
                    request.isContactVerified(),
                    isTncAccepted);

            if (userRole.equals(Roles.ROLE_VENDOR.name()))
                userToRegister.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.REGISTRATION_INITIATED.value());
            else
                userToRegister.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());

            log.debug("Saving user details with data = {}", userToRegister.toString());
            usersRepository.save(userToRegister);

            //Assign Customer Role to user
            Set<UserRoleDtl> userRoleDtlList = updateUserRoles(userToRegister, uuid, Roles.ROLE_USER.name(), userRole);

            log.debug("Saving user role details with data = {}", userRoleDtlList.toString());
            userRoleDtlRepository.saveAll(userRoleDtlList);

            sendRegistrationEmail(emailID, firstName, lastName);

            return userToRegister;
        } else
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_FOR_REGISTRATION.value());
    }

    private void sendRegistrationEmail(String emailID, String firstName, String lastName) {
        emailService.sendEmail(
                emailID,
                env.getProperty("email.registration.title"),
                MessageFormat
                        .format(
                                env.getProperty("email.registration.body"),
                                firstName,
                                (" " + lastName)));
    }

    @Override
    public BaseWrapper updateProDocuments(Long proId, Set<ProDocuments> request) throws ServicesException {

        ProDetails proDetails = validateProId(proId, request);

        Set<ProDocuments> proDocuments = proDetails.getDocuments();
        request.forEach(rpd -> {
            rpd.setProDetails(proDetails);
            rpd.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());
        });

       /* //Call Generic method to deactivate/activate the records based on request object
        commonUtility.toggleActivateStatusIn(proDocuments, request);
*/
        proDocuments.clear();

        proDocuments.addAll(request);

        proDetails.setDocuments(proDocuments);

        proDetailsRepository.save(proDetails);

        return new BaseWrapper(
                proDetails.getDocuments()
                        .stream()
                        .filter(pd -> pd.getActiveStatus() == ActiveStatus.ACTIVE.value())
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public BaseWrapper getProDocuments(Long proId) throws ServicesException {
        ProDetails proDetails = getProDetailsFromOptional(proId);
        return new BaseWrapper(
                proDetails.getDocuments()
                        .stream()
                        .filter(pd -> pd.getActiveStatus() == ActiveStatus.ACTIVE.value())
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public BaseWrapper uploadProDocuments(Long proId, MultipartFile file) throws ServicesException {

        try {
            FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);
            ProDocuments proDocuments = new ProDocuments();
            Optional<FileTypes> fileTypesOptional = FileTypes.valueOf(fileDetailsDTO.getFileType());
            proDocuments.setType(fileTypesOptional.orElse(FileTypes.DEFAULT));
            proDocuments.setUrl(fileDetailsDTO.getPathToFile());

            return new BaseWrapper(proDocuments);
        } catch (IllegalStateException | IOException e) {
            log.error("Error uploading file for user ID = {}. Error = {}", proId, e);
            throw new ServicesException("611");
        }
    }

    @Override
    public Object uploadProDocuments(String proId, FileDetailsDTO fileDetailsDTO) throws ServicesException {

        try {
            //FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);
            ProDocuments proDocuments = new ProDocuments();
            Optional<FileTypes> fileTypesOptional = FileTypes.valueOf(fileDetailsDTO.getFileType());
            proDocuments.setType(fileTypesOptional.orElse(FileTypes.DEFAULT));
            proDocuments.setUrl(fileDetailsDTO.getPathToFile());

            return proDocuments;
        } catch (IllegalStateException e) {
            log.error("Error uploading file for user ID = {}. Error = {}", proId, e);
            throw new ServicesException("611");
        }
    }


    private ProDetails validateProId(Long proId, Collection<?> request) throws ServicesException {
        if (request.isEmpty() || !hasValue(proId) || proId <= 0)
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        return getProDetailsFromOptional(proId);
    }

    private ProDetails getProDetailsFromOptional(Long proId) throws ServicesException {
        Optional<ProDetails> proDetailsOptional = proDetailsRepository.findById(proId);
        if (!proDetailsOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        return proDetailsOptional.get();
    }

    private Set<UserRoleDtl> updateUserRoles(Users users, String uuid, String... roles) {

        Set<UserRoleDtl> userRoleDtlList = new HashSet<>();

        for (String role : roles) {
            UserRolePK rolePk = new UserRolePK();
            rolePk.setRoles(role);
            rolePk.setUserId(uuid);

            UserRoleDtl userRoleDtl = new UserRoleDtl();
            userRoleDtl.setUserLoginDtl(users);
            userRoleDtl.setId(rolePk);
            userRoleDtl.setIsActive((byte) ActiveStatus.ACTIVE.value());
            userRoleDtl.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());

            userRoleDtlList.add(userRoleDtl);
        }

        return userRoleDtlList;
    }

    @Override
    public OAuth2AccessToken doLogIn(String login, String passw, String source, Source dataSource) throws ServicesException {

        // Define Login URL
        final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        log.debug("baseUrl String: " + baseUrl);

//        String logInUrl = MessageFormat.format(baseUrl +
//                RestUrlConstants.PP_USERS + RestUrlConstants.LOGIN
//                + "?username={0}&grant_type=password&source={2}&dataSource={3}&password={1}", login, passw, source, dataSource.name());
        String logInUrl = baseUrl + RestUrlConstants.PP_USERS + RestUrlConstants.LOGIN;
        log.debug("logInUrl = " + logInUrl);

        // Define Headers
        //TODO: Remove Hardcoded Strings
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic b2F1dGhkYjpvYXV0aGRiLXNlY3JldA==");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Set Headers In Request Entity
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("username", login);
        params.add("password", passw);
        params.add("grant_type", "password");
        params.add("source", source);
        params.add("dataSource", dataSource.name());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        // Send Request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<OAuth2AccessToken> response = restTemplate.exchange(logInUrl, HttpMethod.POST, requestEntity,
                OAuth2AccessToken.class);

        // Check response
        if (response.getStatusCode() == HttpStatus.OK) {

            log.debug("Logged In Success. OAuthResponse = " + response.getBody());
            return response.getBody();
        } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {

            log.debug("Unauthorized User Error");
        } else {

            log.debug(
                    "Error Code = " + response.getStatusCode() + ", Error Message = " + response.getStatusCodeValue());
        }

        throw new ServicesException("Invalid username or password supplied");
    }

    @Override
    public OAuth2AccessToken doLogInPro(Long proId) throws ServicesException {
        Users proUser = proDetailsRepository.fetchByUserIdIn(proId);
        return doLogIn(proUser.getEmailId(),
                EncryptionUtils.decrypt(proUser.getAccessCode()), Source.PRO_APP.name(), Source.APP);
    }


    @Override
    public BaseWrapper forgotPassword(SingleValue<String> request, Source source) throws ServicesException {

        String registeredUsername = request.getValue();
        log.debug("received registeredUsername to send reset password link - {}", registeredUsername);
        log.debug("Source ={}", source);

        if (hasValue(registeredUsername)) {
            Set<String> roles = new TreeSet<>();

            if (source.equals(Source.PRO_APP)) {
                roles.add(Roles.ROLE_VENDOR.name());
            } else if (source.equals(Source.CUSTOMER_APP)) {
                roles.add(Roles.ROLE_CUSTOMER.name());
            }

            Users user = usersRepository.fetchByEmailIdOrCntcNoAndRolesInAndActiveStatusNotIn(
                    registeredUsername,
                    registeredUsername,
                    Arrays.asList(
                            ActiveStatus.INACTIVE.value(),
                            ActiveStatus.REGISTRATION_INITIATED.value()), roles);


            if (!hasValue(user)) {
                throw new ServicesException(
                        GeneralErrorCodes.ERR_INVALID_USER_ID.value());
            }

            String contactNo = user.getCntcNo();

            //Send OTP by SMS
            sendOtpBySms(contactNo, user.getEmailId(), OtpTarget.CONTACT, user);

            //Generate Forgot Passcode and save
            String forgotPasswordCode = UUID.randomUUID().toString();
            user.setAccessCode(forgotPasswordCode);
            usersRepository.save(user);

            return new BaseWrapper(forgotPasswordCode);
        } else
            throw new ServicesException("Invalid username supplied");
    }


    private void sendOtpBySms(String contactNo, String emailId, OtpTarget target, Users user) throws ServicesException {
        if (target == OtpTarget.CONTACT && !CheckUtil.isValidPhoneNumber(contactNo))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        //Check if maximum allowed attempts for OTP has exceeded for given contact no
        UserOtp userOtp = null;
        if (hasValue(user)) {
            log.debug("if user exist");
            userOtp = userOtpRepository.findByContactNoAndUserId(contactNo, user.getUserId());
        } else {
            log.debug("if user not exist");
            userOtp = userOtpRepository.findByContactNo(contactNo);
        }

        if (hasValue(userOtp)) {
            if (userOtp.getAttempts() > GlobalConstants.OTP_ALLOWED_MAX_ATTEMPTS)
                throw new ServicesException(
                        GeneralErrorCodes.ERR_MAX_OTP_ATTEMPTS_EXEMPTED.value());

            userOtp.updateAuditableFields(
                    false,
                    !emailId.isEmpty() ? emailId : GlobalConstants.USER_API,
                    ActiveStatus.ACTIVE.value());
        } else {
            userOtp = new UserOtp();
            userOtp.setContactNo(contactNo);
            userOtp.updateAuditableFields(
                    true,
                    !emailId.isEmpty() ? emailId : GlobalConstants.USER_API,
                    ActiveStatus.ACTIVE.value());
        }

        // Update OTP Attempts
        int attempts = userOtp.getAttempts();
        userOtp.setAttempts(attempts + 1);

        String otp = null;
        if (otpActive == true) {
            otp = hasValue(userOtp.getOtp())
                    ? userOtp.getOtp()
                    : commonUtility
                    .generateRandomOtp(
                            GlobalConstants.OTP_LENGTH);
        } else {
            otp = "1111";
        }
        userOtp.setOtp(otp);
        if (hasValue(user)) {
            userOtp.setUser(user);
        }

        //Save OTP data against contactNo
        userOtpRepository.save(userOtp);

        if (otpActive == true) {
            //Send OTP
            switch (target) {
                case EMAIL:
                    if (!emailService.sendEmail(
                            emailId,
                            "OTP to verify email ID",
                            otp))
                        throw new ServicesException(
                                GeneralErrorCodes.ERR_GENERIC_ERROR_MSSG.value());
                    break;

                case CONTACT:
                default:
                    if (!messagingService.sendOTP(
                            otp,
                            contactNo))
                        throw new ServicesException(
                                GeneralErrorCodes.ERR_GENERIC_ERROR_MSSG.value());
                    break;
            }
        }

    }

    @Override
    public BaseWrapper getCustomerHomerScreenDetails() throws IOException {

        HomeScreenVo homescreenVo = new HomeScreenVo();

        List<Services> servicesList = servicesRepository.findByActiveStatus(
                ActiveStatus.ACTIVE.value(),
                Sort.by(Direction.ASC, "sortOrder"));

        List<Advertisements> advertisements = advertisementsRepository.findByActiveStatus(
                ActiveStatus.ACTIVE.value(),
                Sort.by(Direction.ASC, "sortOrder"));
        List<AdvertisementDTO> advertisementsDTO = new AdvertisementConverter().convert(advertisements);

        List<CommercialAdvertisement> commercialAdvertisements = commercialAdvertisementRepository.findByActiveStatus(1, Sort.by(Direction.DESC, "createdTs"));
        CommercialAdvertisement commercialAdvertisement = null;
        if (hasValue(commercialAdvertisements))
            commercialAdvertisement = commercialAdvertisements.get(0);

        //TODO: Add missing data as and when they are implemented
        homescreenVo.updateProperties(
                null,
                authorizationService.fetchLoggedInUser(),
                new ArrayList<>(),
                advertisementsDTO,
                servicesList,
                new ArrayList<>(),
                new ArrayList<>(),
                commercialAdvertisement);

        return new BaseWrapper(homescreenVo);
    }

    //TODO: Find PRO's based on location as well
    @Override
    public BaseWrapper getProListForAService(Long serviceId, ServiceLocationDTO request, Pageable pageableReq) throws IOException, ServicesException {
        final String METHOD_NM = "getProListForAService";
        final String CLASS_NM = getClass().getName();
        log.info(GlobalConstants.LOG.INFO, METHOD_NM, CLASS_NM, request.toString());

        if (!hasValue(serviceId) || serviceId == 0)
            throw new IOException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

        //Prepare SQL Query Data
        List<Long> serviceIds = new ArrayList<>();
        serviceIds.add(serviceId);
        double lat = request.getLatitude();
        double lng = request.getLongitude();
        int activeStatus = ActiveStatus.ACTIVE.value();
        int subscriptionStatus = SubscriptionStatus.ACTIVE.value();
        float serviceLocationRadius = Float.parseFloat(
                configurationRepository.findConfigValueByConfigName(ConfigEnum.SERVICE_LOCATION_RADIUS.name()));
        int availabilityStatus = AvailabilityStatus.AVAILABLE.value();
        float thresholdValue = Float.parseFloat(
                configurationRepository.findConfigValueByConfigName(ConfigEnum.WALLET_DEFAULT_LIMIT.name()));
        log.info("Query Parameters: " +
                        "lat = {}, lng = {}, activeStatus = {}, serviceIds = {}, subscriptionStatus = {},  " +
                        "serviceLocationRadius = {}, availabilityStatus = {}, wallet balance thresholdValue = {}",
                lat, lng, activeStatus, serviceIds, subscriptionStatus, serviceLocationRadius, availabilityStatus, thresholdValue);

        //Get Pageable for PRO Details
        Pageable pageable = null;
        Sort sortReq = pageableReq.getSort();
        if (hasValue(sortReq) && hasValue(sortReq.getOrderFor("ratings")))
            pageable = PageRequest.of(pageableReq.getPageNumber(), pageableReq.getPageSize(), sortReq);
        else
            pageable = PageRequest.of(pageableReq.getPageNumber(), pageableReq.getPageSize());

        List<ProDetails> pros = null;
        Integer totalRecords = 0;
        log.debug("(lat = {}, lng = {}, activeStatus = {}, serviceIds = {}, subscriptionStatus = {}, serviceLocationRadius = {}, availabilityStatus = {}, thresholdValue = {}",
                lat, lng, activeStatus, serviceIds, subscriptionStatus, serviceLocationRadius, availabilityStatus, thresholdValue);
        if (!hasValue(request.getTerm())) {
            // pageable = PageRequest.of(0, 10, Sort.by("ratings").descending().and(Sort.by("startingCost").ascending()));
            pros = proDetailsRepository.fetchByProListingCriterias(lat, lng, activeStatus, serviceIds, subscriptionStatus,
                    serviceLocationRadius, availabilityStatus, thresholdValue, pageable);
            totalRecords = proDetailsRepository.countByProListingCriterias(lat, lng, activeStatus, serviceIds, subscriptionStatus,
                    serviceLocationRadius, availabilityStatus, thresholdValue);
        } else {
            String term = "%" + request.getTerm().trim() + "%";
            log.debug("term being serached = {} for service ID = {}", term, serviceId);
            pros = proDetailsRepository.fetchByNameAndProListingCriterias(term, lat, lng, activeStatus, serviceIds, subscriptionStatus,
                    serviceLocationRadius, availabilityStatus, thresholdValue, pageable);
            totalRecords = proDetailsRepository.countByNameAndProListingCriterias(term, lat, lng, activeStatus, serviceIds, subscriptionStatus,
                    serviceLocationRadius, availabilityStatus, thresholdValue);
        }
        System.out.println("pros fetched Size = " + pros.size());

        Services services = servicesRepository.findById(serviceId).get();

        List<ProDetails> prosWithStartingCost = new ArrayList<>();

        for (ProDetails pro : pros) {
            log.debug("pro without stating cost pro id = {}", pro.getId());
            Long startingCost = getStartingCostForPro(pro.getId(), serviceId);
            log.debug("starting cost = {}", startingCost);
            if (hasValue(startingCost)) {
                if (startingCost > 0) {
                    log.debug("pro added in pro list pro Id = {}", pro.getId());
                    pro.setStartingCost(startingCost);
                    prosWithStartingCost.add(pro);
                }
            }
        }

        if (hasValue(sortReq)
                && hasValue(sortReq.getOrderFor("startingCost"))) {
            Order priceOrder = sortReq.getOrderFor("startingCost");
            Comparator<ProDetails> compareByStartingCost = Comparator.comparing(ProDetails::getStartingCost);
            if (priceOrder.getDirection() == Direction.DESC) {
                Collections.sort(prosWithStartingCost, compareByStartingCost.reversed());
            } else if (priceOrder.getDirection() == Direction.ASC) {
                Collections.sort(prosWithStartingCost, compareByStartingCost);
            }
        }

        //Update isCertified and isPrajekproVerified for each PRO for the given service
        //1. Get the PRO services for all the pros
        List<Long> proIds = pros.stream().map(ProDetails::getId).collect(Collectors.toList());
        List<ProServices> proServices = proServicesRepository.findByProIdInAndServiceId(proIds, serviceId);
        final Map<Long, ProServices> proIdByProServiceMap = proServices.stream().collect(Collectors.toMap(ProServices::getProId, ps -> ps));

        //2. Update total completed projects
        List<Long> prosWithStartingCostIds = prosWithStartingCost.stream().map(ProDetails::getId).collect(Collectors.toList());

        List<AppointmentDetailsRepository.ProJobsCompleted> proJobCompletedDTOS = appointmentService.getJobsCompletedForProIdIn(prosWithStartingCostIds);
        final Map<Long, Integer> proIdToCompletedJobsMap = new HashMap<>();
        proJobCompletedDTOS.forEach(pjc -> proIdToCompletedJobsMap.put(pjc.getProId(), pjc.getJobsCompleted()));

        prosWithStartingCost.forEach(pwsc -> {
            int ttlJobsCompleted = hasValue(proIdToCompletedJobsMap.get(pwsc.getId())) ? proIdToCompletedJobsMap.get(pwsc.getId()) : 0;
            pwsc.setTotalJobsCompleted(ttlJobsCompleted);
        });

        //3. Prepare the final response
        List<ProDetailsDTO> proDetailsDTOList = new ProDetailsDTOConverter().convert(prosWithStartingCost);
        proDetailsDTOList.forEach(proDetailsDTO -> {
            if (proIdByProServiceMap.containsKey(proDetailsDTO.getId())) {
                ProServices proServices1 = proIdByProServiceMap.get(proDetailsDTO.getId());
                proDetailsDTO.setCertified(proServices1.isCertified());
                proDetailsDTO.setPrajekproVerified(proServices1.isPrajekproVerified());
            }
        });
        MetaData<ProDetailsDTO> proDetailsMetaData = new MetaData<>(proDetailsDTOList);

        Pagination page = new Pagination(prosWithStartingCost, totalRecords, pageable);

        return new BaseWrapper(new ProListingVO(
                services.getId(),
                services.getServiceName(),
                proDetailsMetaData), page);
    }


    //TODO: Implement this method
    @Override
    public BaseWrapper getProDetailsForAService(Long serviceId, Long proId) throws JsonParseException, JsonMappingException, IOException, ServicesException {

        Optional<ProDetails> proDetailsOptional = proDetailsRepository.findById(proId);
        if (proDetailsOptional.isPresent()) {

            //1. Service Details
            Services service = servicesRepository.findById(serviceId).get();

            // 2. PRO Details
            ProDetails proDetails = proDetailsOptional.get();
            Long startingCost = getStartingCostForPro(proId, serviceId);
            proDetails.setStartingCost(startingCost);
            ProDetailsDTO proDetailsDTO = new ProDetailsDTOConverter().convert(proDetails);

            //3. Service Items Pricing Details
            List<ProServiceItemsPricing> proServiceItemsPricingList = proDetails.getProServiceItemsPricing();
            java.util.Map<Long, List<ProServiceItemsPricing>> proServiceItemsPricingListByCategoryMap = new HashMap<>();
            java.util.Map<Long, String> categoryDetails = new HashMap<>();
            proServiceItemsPricingList.forEach(
                    psip -> {
                        if (psip.getServices().getId() == serviceId) {
                            ServiceItemCategory category = psip.getServiceItemSubcategory().getServiceItemCategory();
                            Long itemCategoryId = category.getId();

                            categoryDetails.put(itemCategoryId, category.getValue());

                            List<ProServiceItemsPricing> mapProServiceItemsPricingList;
                            if (proServiceItemsPricingListByCategoryMap.containsKey(itemCategoryId))
                                mapProServiceItemsPricingList = proServiceItemsPricingListByCategoryMap.get(itemCategoryId);
                            else
                                mapProServiceItemsPricingList = new ArrayList<>();

                            mapProServiceItemsPricingList.add(psip);
                            proServiceItemsPricingListByCategoryMap.put(itemCategoryId, mapProServiceItemsPricingList);
                        }
                    });

            List<ProServiceItemsPricingDTO> serviceItemsPricingList = new ArrayList<>();
            proServiceItemsPricingListByCategoryMap
                    .keySet()
                    .forEach(
                            cId -> {
                                List<ProServiceItemsPricing> mapProServiceItemsPricingList = proServiceItemsPricingListByCategoryMap.get(cId);
                                List<ItemSubCategoryPricingDetailsDTO> itemSubCatgeoryPricingList = new ProServiceItemSubCategoryDTOConverter()
                                        .convert(mapProServiceItemsPricingList);

                                ProServiceItemsPricingDTO proServiceItemsPricingDTO = new ProServiceItemsPricingDTO(
                                        cId,
                                        categoryDetails.get(cId),
                                        itemSubCatgeoryPricingList);
                                serviceItemsPricingList.add(proServiceItemsPricingDTO);
                            });

            MetaData<ProServiceItemsPricingDTO> serviceItemCategoryMetaData = new MetaData<>(serviceItemsPricingList);

            //4. PRO reviews
            List<ProReviewsDTO> reviewsDTO = new ProReviewsDTOConverter()
                    .convert(proDetails.getReviews());
            MetaData<ProReviewsDTO> reviewsMetaData = new MetaData<>(reviewsDTO);

            //5. PRO Cancellation data
            ProCancellationTime proCancellationTime = proCancellationTimeRepository.fetchTimeByProIdAndServiceId(proId, serviceId);
            ProCancellationTimeDTO proCancellationTimeDTO = null;
            if (!hasValue(proCancellationTime)) {
                proCancellationTimeDTO = new ProCancellationTimeDTO(service);
            } else {
                proCancellationTimeDTO = new ProCancellationTimeDTO(proCancellationTime);
            }
            proCancellationTimeDTO.setCancellationFees(service.getCancellationFees());
            proCancellationTimeDTO.setCancellationFeesUnit(service.getCancellationFeesUnit().getValue());
            proCancellationTimeDTO.setCancellationFeesUnitId(service.getCancellationFeesUnit().getId());
            //5. Prepare response and send
            ProDetailsResponseWrapper response = new ProDetailsResponseWrapper(
                    serviceId,
                    service.getServiceName(),
                    service.getDescription(),
                    proDetailsDTO,
                    serviceItemCategoryMetaData,
                    reviewsMetaData, proCancellationTimeDTO);

            return new BaseWrapper(response);
        } else
            throw new ServicesException("Invalid PRO ID supplied");
    }

    @Override
    public BaseWrapper resetPassword(ResetPasswordDTO request) throws ServicesException {

        if (hasValue(request.getForgotPasswordToken())
                && hasValue(request.getNewPassword())) {

            //Get User Data by Forgot Password Token
            Users user = usersRepository.findByAccessCode(request.getForgotPasswordToken());
            if (!hasValue(user))
                throw new ServicesException(
                        GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

//			//Validate OTP for user's contact no
//			log.debug("Validating OTP = {} for contactNo = {}", request.getOtp(), user.getCntcNo());
//			validateOtpForContactNo(user.getCntcNo(), request.getOtp());
//			log.debug("Validated OTP = {} for contactNo = {}", request.getOtp(), user.getCntcNo());
//
            //Update new password
            String encodedNewPassword = passwordEncoder.encode(
                    request.getNewPassword().trim());
            log.debug("Updating encodedPassword = {} for user ID = {}", encodedNewPassword, user.getUserId());
            user.setPassword(encodedNewPassword);
            user.setActiveStatus(ActiveStatus.ACTIVE.value());
            usersRepository.save(user);
            log.debug("Updated encodedPassword = {} for user ID = {}", encodedNewPassword, user.getUserId());

            return new BaseWrapper();
        } else {
            throw new ServicesException(
                    GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
    }

    @Override
    public BaseWrapper sendOtp(SingleValue<String> value, OtpTarget target, Source source) throws ServicesException {

        Users user = fetchUserIfExists(value.getValue(), source);
        // Users user = authorizationService.fetchLoggedInUser();
        log.debug("user has value or not = {}", hasValue(user));
        sendOtpBySms(user.getCntcNo(), user.getEmailId(), target, user);

        return new BaseWrapper();
    }

    @Override
    public BaseWrapper verifyUser(SingleValue<String> value, OtpTarget target) throws ServicesException {

        sendOtpBySms(value.getValue(), "", target, null);

        return new BaseWrapper();
    }

    private Users fetchUserIfExists(String userName, Source source) throws ServicesException {

        Users user = null;
        Set<String> roles = new TreeSet<>();
        if (hasValue(source)) {
            if (source.equals(Source.PRO_APP)) {
                roles.add(Roles.ROLE_VENDOR.name());
            } else if (source.equals(Source.CUSTOMER_APP)) {
                roles.add(Roles.ROLE_CUSTOMER.name());
            }

            user = usersRepository.fetchByEmailIdOrCntcNoAndRolesInAndActiveStatusNotIn(
                    userName,
                    userName,
                    Arrays.asList(
                            ActiveStatus.INACTIVE.value(),
                            ActiveStatus.REGISTRATION_INITIATED.value()), roles);


        } else {
            user = usersRepository.fetchByEmailIdOrCntcNoAndActiveStatusNotIn(userName, userName,
                    Arrays.asList(
                            ActiveStatus.INACTIVE.value(),
                            ActiveStatus.REGISTRATION_INITIATED.value()));
        }
        if (!hasValue(user))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());

        return user;
    }


    @Override
    public BaseWrapper verifyOtp(OtpDTO request, String target, Source source) throws ServicesException {

        if (!hasValue(request.getUsername())
                || !hasValue(request.getOtp()))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());


        Users user = fetchUserIfExists(request.getUsername(), source);

        validateOtpForContactNo(user.getCntcNo(), request.getOtp(), user);

        if (NumberUtils.isCreatable(target)) {
            user.setIsContactVerified(true);
        } else {
            user.setIsEmailVerified(true);
        }

        return new BaseWrapper();
    }


    @Override
    public BaseWrapper verifyUserOtp(OtpDTO request, String target) throws ServicesException {
        if (!hasValue(request.getUsername())
                || !hasValue(request.getOtp()))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        validateOtpForContactNo(request.getUsername(), request.getOtp(), null);

        return new BaseWrapper();
    }

    @Override
    public ResponseEntity<ByteArrayResource> getDownloadImage(String id, DownloadSource source) throws IOException, ServicesException, com.itextpdf.text.DocumentException {

        if (!hasValue(id) || !hasValue(source))
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        DownloadImageDTO imageInfo = null;
        switch (source) {
            case ADVERTISEMENT_IMAGE:
                log.debug("inside download advertisement image");
                imageInfo = masterService.downloadAdvertisementImage(Long.valueOf(id));
                break;

            case USER_PROFILE_IMAGE:
                log.debug("inside download User profile Image");
                imageInfo = userService.downloadUserProfileImage(id);
                break;

            case PRO_PROFILE_IMAGE:
                log.debug("inside download Pro profile Image");
                imageInfo = userService.downloadProProfileImage(Long.valueOf(id));
                break;

            case PRO_DOCUMENT:
                log.debug("inside download Pro document");
                imageInfo = userService.downloadProDocuments(id);
                break;

            case APPOINTMENT_INVOICE:
                log.debug("Inside Appointment Invoice");
                InvoiceDtl invoiceDtl = appointmentService.selectAppointmentInvoiceDtl(Long.valueOf(id));
                imageInfo = new DownloadImageDTO();
                imageInfo.setFilePath(PDFUtils.generateInvoice(invoiceDtl, fileUploadPath));
                imageInfo.setImgExtn("pdf");
                imageInfo.setDisplayName("Invoice");
                break;

            case APK:
                log.info("Downalod APK");
                imageInfo = new DownloadImageDTO();
                imageInfo.setFilePath(apkDownloadPath);
                imageInfo.setImgExtn("apk");
                imageInfo.setDisplayName("Prajekpro_Beta.apk");
                break;

            case MESSAGE_ATTACHMENT:
                log.info("Download Message Attachment for ID = {}", id);
                imageInfo = chatServiceImpl.getMessageAttachmentInfo(id);
                log.info("imageInfo = {}", imageInfo.toString());
                break;

            case PRO_SERVICE_DOCS:
                log.info("Download PRO_SERVICE_DOCS for ID = {}", id);
                imageInfo = proServicesService.getProServiceDocDetails(id);
                log.info("imageInfo = {}", imageInfo.toString());
                break;

            case APPT_CHECK_IN:
            case APPT_CHECK_OUT:
                log.info("Download PRO_SERVICE_DOCS for ID = {}", id);
                imageInfo = appointmentService.getApptDocs(id);
                log.info("imageInfo = {}", imageInfo.toString());
                break;

            default:
                imageInfo.setFilePath(fileUploadPath);
                imageInfo.setDisplayName(PP_DEFAULT_IMAGE);
        }

        byte[] bytes = Files.readAllBytes(Paths.get(imageInfo.getFilePath()));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + imageInfo.getDisplayName());
        responseHeaders.add("file-extension", imageInfo.getImgExtn());
        return new ResponseEntity(bytes, responseHeaders, HttpStatus.OK);
    }

    @Override
    public BaseWrapper uploadImage(String id, DownloadSource source, MultipartFile file) throws IOException, ServicesException {

        FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);
        Object responseObject = null;
        switch (source) {
            case USER_PROFILE_IMAGE:
                userService.uploadProfileImage(id, fileDetailsDTO);
                responseObject = id;
                break;
            case PRO_PROFILE_IMAGE:
                userService.uploadProProfileImage(Long.valueOf(id), fileDetailsDTO);
                responseObject = id;
                break;
            case PRO_DOCUMENT:
                responseObject = uploadProDocuments(id, fileDetailsDTO);
                break;
//            case MESSAGE_ATTACHMENT:
//                responseObject = uploadMessageAttachment
        }

        return new BaseWrapper(responseObject);
    }

    @Override
    public BaseWrapper getSubscriptionDetails(Long proId) throws ServicesException {

        Optional<ProDetails> proDetailsOptional = proDetailsRepository.findById(proId);
        if (!proDetailsOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

        ProDetails proDetails = proDetailsOptional.get();
        Users user = proDetails.getUserDetails();
        if (!user.getActiveStatus().equals(ActiveStatus.APPROVAL_PENDING.value()))
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

        ProSubscription proSubscription = proSubscriptionRepository.getSubscriptionByProId(proId, ActiveStatus.ACTIVE.value());
        MasterSubscription masterSubscription = proSubscription.getMasterSubscription();
        ProSubscriptionResponseDTO proSubscriptionResponseDTO = new ProSubscriptionResponseDTO();
        proSubscriptionResponseDTO.setProName(proDetails.getUserDetails().getFirstNm());
        proSubscriptionResponseDTO.setApplicationNo(String.valueOf(proDetails.getApplicationNo()));
        proSubscriptionResponseDTO.setAmountPaid(masterSubscription.getSubscriptionAmount());
        proSubscriptionResponseDTO.setDateOfPurchase(proSubscription.getDateOfSubscription());
        if (masterSubscription != null) {
            proSubscriptionResponseDTO.setCurrencySymbol(masterSubscription.getCurrency().getSymbol());
        }

        ProSubscriptionResponseLoginDTO proSubscriptionResponseLoginDTO =
                new ProSubscriptionResponseLoginDTO(proSubscriptionResponseDTO, doLogInPro(proDetails.getId()));

        return new BaseWrapper(proSubscriptionResponseLoginDTO);
    }


    private void validateOtpForContactNo(String contactNo, String otp, Users user) throws ServicesException {

        UserOtp userOtp = null;
        if (hasValue(user)) {
            log.debug("if user exist");
            userOtp = userOtpRepository.findByContactNoAndUserId(contactNo, user.getUserId());
        } else {
            log.debug("if user not exist");
            userOtp = userOtpRepository.findByContactNo(contactNo);
        }

        if (!hasValue(userOtp))
            throw new ServicesException(
                    GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

        if (otp
                .equals(
                        userOtp.getOtp())) {
            userOtpRepository.delete(userOtp);
        } else
            throw new ServicesException("701");
    }

    @Override
    public BaseWrapper addEnquiry(EnquiryDetailsDTO request) {
        EnquiryDetails enquiry = new EnquiryDetails(request);
        enquiry.updateAuditableFields(true, "WEBSITE", ActiveStatus.ACTIVE.value());

        enquiryDetailsRepository.save(enquiry);

        emailService.sendEmail(
                request.getEmailId().trim(),
                "Inquiry Mail",
                "Your Inquiry is successfully registered with us");

        return new BaseWrapper("Enquiry Added Successfully");
    }

    @Override
    public BaseWrapper getCouponDetails(Long proId, String couponCode) throws ServicesException {
        CouponCodeDetails couponCodeDetails = couponCodeDetailsRepository.findByCouponCodeIgnoreCaseAndActiveStatus(couponCode, ActiveStatus.ACTIVE.value());
        if (!hasValue(couponCodeDetails))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        //Check if coupon is already redeemed, unless coupon can be redemeed unlimited times
        int validityPerUser = couponCodeDetails.getValidityPerUser();
        if (validityPerUser != -1) {
            Users proUserDetails = proDetailsRepository.fetchByUserIdIn(proId);
            List<CouponRedemptionDetails> couponRedemptionDetails = couponRedemptionDetailsRepository.findByCouponCodeDetails_IdAndUser_UserIdAndActiveStatus(couponCodeDetails.getId(), proUserDetails.getUserId(), ActiveStatus.ACTIVE.value());

            if (hasValue(couponRedemptionDetails) && couponRedemptionDetails.size() >= validityPerUser)
                throw new ServicesException(705);
        }

        BaseWrapper response;
        switch (couponCodeDetails.getCouponCodeType()) {
            case SUBSCRIPTION:
                MasterSubscriptionDTO masterSubscription = subscriptionService.getSubscriptionById(Long.parseLong(couponCodeDetails.getMetaData().trim()));
                response = new BaseWrapper(masterSubscription);
                break;

            default:
                throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        return response;
    }

    @Override
    public BaseWrapper getStaticContent(StaticContent request) throws ServicesException {
        String contentId = request.getContentId();
        StaticContentDTO staticContentDTO = new StaticContentDTO();

        if (hasValue(contentId)) {
            StaticContent staticContent=staticContentRepository.findByContentId(contentId);
            BeanUtils.copyProperties(staticContent,staticContentDTO);
            if(contentId.equalsIgnoreCase("CUST_BOOK_APPT")) {
                String message = MessageFormat.format(staticContentDTO.getContent(),
                        request.getContentValue().get("MIN"), request.getContentValue().get("AMOUNT"));
                staticContentDTO.setContent(message);
            }
            return new BaseWrapper(staticContentDTO);
        }
        else
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
    }

    public String generateEncodedPass(String param) {
        log.info("param:{}", param);
        return passwordEncoder.encode(param);
    }


    private Long getStartingCostForPro(Long id, Long serviceId) {
        Long startingCost = proServiceItemsPricingRepository.fetchMinPriceByProIdAndServiceId(id, serviceId);
        return startingCost;
    }

    private class ProServiceItemSubCategoryDTOConverter extends DTOConverter<ProServiceItemsPricing, ItemSubCategoryPricingDetailsDTO> {

        @Override
        public ItemSubCategoryPricingDetailsDTO convert(ProServiceItemsPricing input) {

            return dtoService.createItemSubCategoryPricingDetailsDTO(input);
        }
    }

    private class ProServiceDTOConverter extends DTOConverter<Services, ProServicesDTO> {

        @Override
        public ProServicesDTO convert(Services input) {

            return dtoService.createProServiceDTO(input);
        }
    }

    private class ProReviewsDTOConverter extends DTOConverter<ProReviews, ProReviewsDTO> {

        @Override
        public ProReviewsDTO convert(ProReviews input) {

            return dtoService.createProReviewsDTO(input);
        }
    }

    private class AdvertisementConverter extends DTOConverter<Advertisements, AdvertisementDTO> {

        @Override
        public AdvertisementDTO convert(Advertisements input) {

            return dtoService.createAdvertisementDTO(input);
        }
    }

    private class ProDetailsDTOConverter extends DTOConverter<ProDetails, ProDetailsDTO> {

        @Override
        public ProDetailsDTO convert(ProDetails input) {

            return dtoService.createProDetailsDTO(input);
        }
    }
}
