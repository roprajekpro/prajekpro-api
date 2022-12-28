package com.prajekpro.api.service.impl;

import com.google.gson.*;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.prajekpro.api.util.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.repository.*;
import com.safalyatech.common.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.provider.endpoint.*;
import org.springframework.stereotype.*;
import org.springframework.web.*;
import org.springframework.web.multipart.*;

import javax.transaction.*;
import java.io.*;
import java.security.*;
import java.util.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
@Transactional(rollbackOn = Throwable.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ProDetailsRepository proDetailsRepository;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private UserRoleDtlRepository userRoleDtlRepository;
    @Autowired
    private PushNotificationRepository pushNotificationRepository;
    @Autowired
    PublicServiceImpl publicServiceImpl;
    @Autowired
    private ProDetailDocumentRepository proDetailDocumentRepository;

    @Autowired
    private TokenEndpoint tokenEndPoint;

    @Value("${file.upload.path}")
    private String fileUploadPath;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Value("${notification_update_pro_status}")
    private String updateProStatus;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private PushNotificationService pushNotificationService;

    @Override
    public BaseWrapper updateUserProfileInfo(PPRegisterVO request) throws ServicesException, DuplicateRecordException {

        try {
            String emailID = request.getEmailId();
            if (!hasValue(emailID))
                throw new ServicesException("603");

            String contactNo = request.getContactNo();
            if (!hasValue(contactNo))
                throw new ServicesException("604");
            Users user = null;
            if (hasValue(request.getSource()) && request.getSource().name().equals(Source.WEB.name())) {
                ProDetails proDetails = proDetailsRepository.findById(request.getProId()).get();
                user = proDetails.getUserDetails();
            } else {
                user = usersRepository.findById(authorizationService
                        .fetchLoggedInUser()
                        .getUserId())
                        .get();
            }

            //Check if PRO details exists for the given user ID
            ProDetails proDetailsOptional = proDetailsRepository.findByUserDetails_UserId(user.getUserId());
            if (hasValue(proDetailsOptional)) {
                //Update PRO related details
                proDetailsOptional.setExperienceInYears(request.getExperienceInYears());
                proDetailsOptional.setAboutText(request.getAboutText());
                proDetailsRepository.save(proDetailsOptional);
            }

          /*  Users user = usersRepository.findById(authorizationService
                    .fetchLoggedInUser()
                    .getUserId())
                    .get();*/

            if (!user.getEmailId().equals(emailID)) {
                Integer duplicateCount = usersRepository.countByEmailIDWhereUserIDNotIn(emailID, user.getUserId());
                log.debug("emailID duplicateCount = {}", duplicateCount);

                if (hasValue(duplicateCount)
                        && duplicateCount > 0) {
                    throw new DuplicateRecordException(GeneralErrorCodes.ERR_EMAIL_ID_EXISTS.value());
                }
            }

            if (!user.getCntcNo().equals(request.getContactNo())) {
                Integer duplicateCount = usersRepository.countByContactNoWhereUserIDNotIn(request.getContactNo(), user.getUserId());
                log.debug("contactNumber duplicateCount = {}", duplicateCount);

                if (hasValue(duplicateCount)
                        && duplicateCount > 0) {
                    throw new DuplicateRecordException(GeneralErrorCodes.ERR_CONTACT_NO_EXISTS.value());
                }
            }

            user.setFirstNm(request.getFirstName());
            user.setLastNm(request.getLastName());
            user.setLocationText(request.getAddress());
            user.setLandLineNo(request.getLandlineNo());
//            String lat = null == request.getLatitude() ? null : Float.toString(request.getLatitude());
            user.setLocLatitude(request.getLatitude());
//            String lng = null == request.getLongitude() ? null : Float.toString(request.getLongitude());
            user.setLocLongitude(request.getLongitude());

            // set ActiveStatus for User.
            if (user.getActiveStatus() == ActiveStatus.REASSESSMENT.value()) {
                String updateProStatusMessageNotification = String.format(updateProStatus, ActiveStatus.valueOf(user.getActiveStatus()).get().name(), ActiveStatus.APPROVAL_PENDING.name());
                user.setActiveStatus(ActiveStatus.APPROVAL_PENDING.value());
                sendNotification(user, "Registration Status Update", updateProStatusMessageNotification);
            }

            if (!user.getEmailId().equalsIgnoreCase(emailID.trim())) {
                user.setEmailId(emailID);
                user.setIsEmailVerified(request.isEmailVerified());
                //request.setEmailVerified(false);
            }
            if (!user.getCntcNo().equalsIgnoreCase(contactNo.trim())) {
                user.setCntcNo(contactNo);
                user.setIsContactVerified(request.isContactVerified());
                // request.setContactVerified(false);
            }
            usersRepository.save(user);
            return new BaseWrapper(request);
        } catch (DataIntegrityViolationException e) {
            log.error("Possibly Duplicate Entry Found. Exception = {}", e);
            throw new DuplicateRecordException(GeneralErrorCodes.ERR_DUPLICATE_RECORD_EXISTS.value());
        }
    }

    private void sendNotification(Users notificationToUser, String notificationTitle, String notificationBody) {

        List<String> tokenListByUserId = pushNotificationRepository.findTokenListByUserId(notificationToUser.getUserId());
        if (!tokenListByUserId.isEmpty()) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest(
                    notificationTitle, notificationBody, tokenListByUserId, NotificationType.USER_STATUS);

            //save notification to DB
            UserNotificationMetadataDTO userNotificationMetadataDTO = new UserNotificationMetadataDTO(pushNotificationRequest.getMessage(), pushNotificationRequest.getNotificationTime());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonMetadata = gson.toJson(userNotificationMetadataDTO);

            UserNotification userNotification = new UserNotification(notificationToUser, pushNotificationRequest.getTitle(), NotificationType.USER_STATUS,
                    jsonMetadata, false, false);
            userNotification.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            userNotificationRepository.save(userNotification);

            //cal Async to send notification
            pushNotificationService.sendPushNotificationToMultipleToken(getPayloadDataForNotification(),
                    pushNotificationRequest);
        }
    }

    private Map<String, String> getPayloadDataForNotification() {
        HashMap<String, String> data = new HashMap<>();
        data.put("", "");
        return data;
    }


    @Override
    public BaseWrapper uploadProfileImage(String id, FileDetailsDTO fileDetailsDTO) throws ServicesException {

        log.debug("inside file uploading service method");
        // String loggedInUserID = authorizationService.fetchLoggedInUser().getUserId();

        try {

           /* log.debug("inside file uploading try block");
            FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);
*/
            Users user = usersRepository.findById(id).get();
            log.debug("user to string = {}", user.toString());
            user.updateProfileImageDetails(fileDetailsDTO);
            log.debug("updated profile image details");
            user.updateAuditableFields(false, user.getEmailId(), ActiveStatus.ACTIVE.value());
            usersRepository.save(user);

            return new BaseWrapper();
        } catch (IllegalStateException e) {
            log.error("Error uploading file for user ID = {}. Error = {}", id, e);
            throw new ServicesException("611");
        }
    }

    @Override
    public DownloadImageDTO downloadUserProfileImage(String loggedInUserID) throws ServicesException {

        Users users = usersRepository.findById(loggedInUserID).get();
        DownloadImageDTO imageInfo = new DownloadImageDTO();
        imageInfo.setFilePath(CommonUtility.getUserProfileImageFilePath(fileUploadPath, users));
        imageInfo.setDisplayName(users.getPrflImgDisplayNm());
        imageInfo.setImgExtn(users.getPrflImgExtn());
        return imageInfo;
    }

    @Override
    public BaseWrapper changePassword(ChangePasswordDTO request) throws ServicesException {

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        if (hasValue(oldPassword) && hasValue(newPassword)) {
            String userId = authorizationService.fetchLoggedInUser().getUserId();
            Users user = usersRepository.findById(userId).get();

            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                usersRepository.save(user);
            } else
                throw new ServicesException(GeneralErrorCodes.ERR_INVALID_PASSWORD_SUPPLIED.value());
        } else
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        return new BaseWrapper();
    }

    @Override
    public BaseWrapper getCustomerUsers(Pageable pageable) {
        List<String> rolesToGetList = new ArrayList<String>();
        rolesToGetList.add(Roles.ROLE_CUSTOMER.name());
        Page<Users> usersListPage = userRoleDtlRepository.findUsersByRolesIn(rolesToGetList, pageable);
        List<Users> usersList = usersListPage.getContent();
        List<UserDetailsVO> finalUsersList = new ArrayList<>();
        for (Users users : usersList)
            finalUsersList.add(new UserDetailsVO(users));
        return new BaseWrapper(finalUsersList);
    }

    @Override
    public BaseWrapper uploadProfileImage(MultipartFile file) throws ServicesException {

        log.debug("inside file uploading service method");
        String loggedInUserID = authorizationService.fetchLoggedInUser().getUserId();

        try {

            log.debug("inside file uploading try block");
            FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);

            Users user = usersRepository.findById(loggedInUserID).get();
            log.debug("user to string = {}", user.toString());
            user.updateProfileImageDetails(fileDetailsDTO);
            log.debug("updated profile image details");
            user.updateAuditableFields(false, user.getEmailId(), ActiveStatus.ACTIVE.value());
            usersRepository.save(user);

            return new BaseWrapper();
        } catch (IllegalStateException | IOException e) {
            log.error("Error uploading file for user ID = {}. Error = {}", loggedInUserID, e);
            throw new ServicesException("611");
        }
    }


    @Override
    public BaseWrapper uploadProProfileImage(Long proId, MultipartFile file) throws ServicesException {

        try {
            FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);
            Users user = proDetailsRepository.fetchByUserIdIn(proId);
            user.updateProfileImageDetails(fileDetailsDTO);
            user.updateAuditableFields(false, user.getEmailId(), user.getActiveStatus());
            usersRepository.save(user);

            return new BaseWrapper();
        } catch (IllegalStateException | IOException e) {
            log.error("Error uploading file for user ID = {}. Error = {}", proId, e);
            throw new ServicesException("611");
        }
    }

    @Override
    public BaseWrapper uploadProProfileImage(Long proId, FileDetailsDTO fileDetailsDTO) throws ServicesException {

        try {
            Users user = proDetailsRepository.fetchByUserIdIn(proId);
            user.updateProfileImageDetails(fileDetailsDTO);
            user.updateAuditableFields(false, user.getEmailId(), user.getActiveStatus());
            usersRepository.save(user);

            return new BaseWrapper();
        } catch (IllegalStateException e) {
            log.error("Error uploading file for user ID = {}. Error = {}", proId, e);
            throw new ServicesException("611");
        }
    }

    @Override
    public DownloadImageDTO downloadProProfileImage(Long proId) throws ServicesException {

        Users users = proDetailsRepository.fetchByUserIdIn(proId);
        DownloadImageDTO imageInfo = new DownloadImageDTO();
        imageInfo.setFilePath(users.getPrflImgSaveDirNm() + users.getPrflImgSavedNm());
        imageInfo.setDisplayName(users.getPrflImgDisplayNm());
        imageInfo.setImgExtn(users.getPrflImgExtn());
        return imageInfo;


       /* byte[] bytes = Files.readAllBytes(Paths.get(filePath));

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("content-disposition", "attachment; filename=" + users.getPrflImgDisplayNm());
            responseHeaders.add("file-extension", users.getPrflImgExtn());

            return new ResponseEntity(bytes, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while downloading profile image for user ID = {}. Error = {}", proId, e);
            throw new ServiceException(e.getMessage());
        }*/
    }

    @Override
    public BaseWrapper logoutUser(String deviceId) {

        pushNotificationRepository.deleteUserNotificationTokenByUserIdAndDeviceId(authorizationService.fetchLoggedInUser().getUserId(), deviceId);
        return new BaseWrapper();
    }

    @Override
    public DownloadImageDTO downloadProDocuments(String id) {

        log.debug("fetch pro documents");
        ProDocuments proDocument = proDetailDocumentRepository.getOne(Long.valueOf(id));
        log.debug("pro documents = {}", proDocument);
        DownloadImageDTO imageInfo = new DownloadImageDTO();
        String url = proDocument.getUrl();
        log.debug("file url = {}", url);
        imageInfo.setFilePath(url);
        String extension = url.substring(url.lastIndexOf("."));
        log.debug("file extension = {}", extension);
        imageInfo.setImgExtn(extension);
        String displayName = null;
        if (url.contains("/")) {
            displayName = url.substring(url.lastIndexOf("/"));
        }
        if (url.contains(""))
            log.debug("file display name = {}", displayName);
        imageInfo.setDisplayName(displayName);

        return imageInfo;
    }


    @Override
    public OAuth2AccessToken loginUser(Principal principal, Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        parameters.keySet().forEach(key -> log.info("{} = {}", key, parameters.get(key)));

        //Get Roles list to compare based on login source
        Set<String> roleMasterDtlSet = CommonUtil.getRoles(parameters.get("source"));
//        roleMasterDtlSet.forEach(roleMasterDtl -> log.info("{}", roleMasterDtl.getRoles()));

        String username = parameters.get("username");
        List<Users> usersList = usersRepository.fetchByEmailIdOrCntcNoAndRolesIn(
                username, username, roleMasterDtlSet, null);
        log.info("does user exists = {}", hasValue(usersList.isEmpty()));
        if (!hasValue(usersList) || usersList.size() > 1)
            throw new UsernameNotFoundException(GeneralErrorCodes.ERR_INVALID_USERNAME.value());

        Users user = usersList.get(0);
        List<Integer> restrictedStatusList = Arrays.asList(
                ActiveStatus.INACTIVE.value(),
                ActiveStatus.ARCHIEVED.value(),
                ActiveStatus.REGISTRATION_INITIATED.value()
        );

        if (restrictedStatusList.contains(user.getActiveStatus()))
            throw new UsernameNotFoundException(GeneralErrorCodes.ERR_ADMIN_DEACTIVATED.value());

        String dataSource = hasValue(parameters.get("dataSource")) ? parameters.get("dataSource") : Source.APP.name();
        parameters.put("username", String.format("%s@#%s@#%s", username, parameters.get("source"), dataSource));
        ResponseEntity<OAuth2AccessToken> response;
        try {
            response = tokenEndPoint.postAccessToken(principal, parameters);
            if (response.getStatusCode() == HttpStatus.OK)
                return response.getBody();
            else
                throw new UsernameNotFoundException(GeneralErrorCodes.ERR_INVALID_PASSWORD_SUPPLIED.value());
        } catch (InvalidGrantException e) {
            throw new UsernameNotFoundException(GeneralErrorCodes.ERR_INVALID_PASSWORD_SUPPLIED.value());
        } catch (Exception e) {
            log.error("Error getting access token = {}", e);
            throw new UsernameNotFoundException(GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());
        }
    }

}
