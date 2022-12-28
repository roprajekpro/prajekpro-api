package com.prajekpro.api.service.impl;

import com.google.gson.*;
import com.prajekpro.api.converters.*;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.domain.specifications.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.repository.*;
import com.safalyatech.common.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Slf4j
@Service
public class ProServiceImpl implements ProService {


    @Value("${notification_update_pro_status}")
    private String updateProStatus;

    @Autowired
    private ProDetailsRepository proDetailsRepository;
    @Autowired
    private ProServicesLocationsRepository proServicesLocationsRepository;
    @Autowired
    private ProSubscriptionRepository proSubscriptionRepository;
    @Autowired
    private AppointmentDetailsRepository appointmentDetailsRepository;
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private ProReviewsRepository proReviewsRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PushNotificationRepository pushNotificationRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private ProServiceDocumentsRepository proServiceDocumentsRepository;
    @Autowired
    private ProServicesRepository proServicesRepository;

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private DTOFactory dtoService;


    @Override
    public BaseWrapper getProList(ProSearchRequestBodyDTO request, Pageable pageable) {


        Sort sort = Sort.by("createdTs").descending();
        if (pageable.isPaged()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        } else {
            pageable = PageRequest.of(0, 10, sort);
        }

        Page<ProDetails> proDetailsPage = null;
        if (hasValue(request.getLatLongList()) || hasValue(request.getActiveStatus()) || hasValue(request.getRatings()) ||
                hasValue(request.getSubscriptionDateFrom()) || hasValue(request.getSubscriptionDateTill()) || hasValue(request.getSubscriptionStatus())) {
            Specification<ProDetails> spec = new SearchProSpecification(request);
            proDetailsPage = proDetailsRepository.findAll(spec, pageable);
        } else {
            proDetailsPage = proDetailsRepository.findAll(pageable);
        }


        List<ProDetails> proDetailsList;

        if (!proDetailsPage.hasContent()) {
            proDetailsList = new ArrayList<>();
        }
        proDetailsList = proDetailsPage.getContent();

        List<ProListDTO> proList = new ArrayList<>();

        if (proDetailsList.isEmpty()) {
            return new BaseWrapper(proList);
        }

        for (ProDetails details : proDetailsList) {

            ProListDTO pro = new ProListDTO();

            Long proId = details.getId();
            log.debug("Pro details into pro DTO List pro Id = {}", proId);

            //set proId
            pro.setProId(proId);

            //set pro full name
            pro.setFullName(details.getUserDetails().getFullName());

            //set pro status-remark
            pro.setStatusRemark(details.getUserDetails().getStatusRemark());

            // set pro ratings
            pro.setRatings(details.getRatings());

            //set pro service locations
            LocationDetailsDTO proLocation = new LocationDetailsDTO(details);
            pro.setProServicesLocations(proLocation);

            // set subscription status
            SubscriptionStatus status = proSubscriptionRepository.findSubscriptionStatusByProId(proId, ActiveStatus.ACTIVE.value());
            if (hasValue(status)) {
                pro.setSubscriptionStatus(status);
            }

            // set subscription expiry date and date of subscription
            ProSubscription currentProSubscription = proSubscriptionRepository.findByProIdAndModifiedTs(proId, ActiveStatus.ACTIVE.value());
            if (hasValue(currentProSubscription)) {
                pro.setDateOfSubscription(currentProSubscription.getDateOfSubscription());
                pro.setSubscriptionExpiryDate(currentProSubscription.getSubscriptionExpiresOn());
            }

            // set all Appointments
            int totalAppointments = appointmentDetailsRepository.countOfTotalAppointmentsByProId(proId);
            pro.setTotalAppointments(totalAppointments);

            // set today's Appointments
            int todaysAppointments = appointmentDetailsRepository.countOfTodaysAppointmentsByProId(proId);
            pro.setTodaysAppointments(todaysAppointments);

            // set pro is Active or Not
            pro.setActiveStatus(details.getUserDetails().getActiveStatus());

            //set profile file path
            pro.setPrflImgSaveDirNm(details.getUserDetails().getPrflImgSaveDirNm());
            pro.setPrflImgSavedNm(details.getUserDetails().getPrflImgSavedNm());

            //set pro documents
            pro.setDocuments(details.getDocuments());

            proList.add(pro);
        }

        Pagination pagination = new Pagination(proList, proDetailsPage.getTotalElements(), pageable);
        return new BaseWrapper(proList, pagination);
    }

    @Override
    public BaseWrapper getProDetails(Long proId) throws ServicesException {

        if (!hasValue(proId) || proId == 0)
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        Optional<ProDetails> proDetailsOptional = proDetailsRepository.findById(proId);
        if (!proDetailsOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        ProDetails proDetail = proDetailsOptional.get();

        // pro basic details
        Users users = proDetailsRepository.fetchByUserIdIn(proId);
        PPRegisterVO registerVO = new PPRegisterVO();
        registerVO.setProId(proId);
        registerVO.setFirstName(users.getFirstNm());
        registerVO.setLastName(users.getLastNm());
        registerVO.setEmailId(users.getEmailId());
        registerVO.setContactNo(users.getCntcNo());
        registerVO.setLandlineNo(users.getLandLineNo());
        registerVO.setLatitude(users.getLocLatitude());
        registerVO.setLongitude(users.getLocLongitude());
        registerVO.setAddress(users.getLocationText());
        registerVO.setActiveStatus(users.getActiveStatus());
        registerVO.setContactVerified(users.getIsContactVerified());
        registerVO.setEmailVerified(users.getIsEmailVerified());
        registerVO.setTncAccepted(users.isTncAccepted());
        registerVO.setVatRegistered(proDetail.isVatRegistered());
        registerVO.setVatNo(proDetail.getVatNo());

        long totalAppointments = 0l;

        List<ProvidedServiceDetailsDTO> providedServiceDetails = getProServices(proId, totalAppointments);

        List<ProReviews> proReviews = proDetail.getReviews();
        List<ProReviewsDTO> proReviewsDTOS = new ArrayList<>();
        Double averageRating = 0d;
        for (ProReviews reviews : proReviews) {
            averageRating = averageRating + reviews.getStarRating();
            proReviewsDTOS.add(new ProReviewsDTO(reviews));
        }
        averageRating = averageRating / proReviews.size();

        log.debug("average rating = {}", averageRating);
        // averageRating = 4.5d;
        if (averageRating.isNaN()) {
            averageRating = 0d;
        }
        ProjectOverviewDTO projectOverviewDTO = new ProjectOverviewDTO(totalAppointments, averageRating, proReviews.size());
        ProDetailsAndOverviewsDTO proDetailsAndOverviewsList = new ProDetailsAndOverviewsDTO(registerVO, providedServiceDetails, projectOverviewDTO);

        return new BaseWrapper(proDetailsAndOverviewsList);
    }

    @Override
    public List<ProvidedServiceDetailsDTO> getProServices(Long proId, long totalAppointments) {
        //pro overviews
//        Set<Services> proServices = servicesRepository.fetchServicesByProIdIn(proId, Arrays.asList(ActiveStatus.ACTIVE.value()));
        List<ProServices> proServices = proServicesRepository.fetchProActiveServiceByProId(proId, ActiveStatus.ACTIVE.value());

        List<AppointmentDetails> appointmentDetailsList = appointmentDetailsRepository.findByBookedFor_Id(proId);
        log.debug("size of appointment list booked for logged in user = {}", appointmentDetailsList.size());

        List<ProvidedServiceDetailsDTO> providedServiceDetails = new ArrayList<>();

        Long appointmentCount = 0l;

        Map<Long, String> serviceIdToNameMap = new HashMap<>();
        Map<Long, String> serviceIdToIconMap = new HashMap<>();
        if (hasValue(proServices)) {
            Page<Services> servicesPg = servicesRepository.findAllByActiveStatus(ActiveStatus.ACTIVE.value(), null);
            if (servicesPg.hasContent()) {
                serviceIdToNameMap = servicesPg.get().collect(Collectors.toMap(Services::getId, Services::getServiceName));
                serviceIdToIconMap = servicesPg.get().collect(Collectors.toMap(Services::getId, Services::getServiceIcon));
            }
        }

        for (ProServices proService : proServices) {
            Long serviceId = proService.getServiceId();

            for (AppointmentDetails appointmentDetail : appointmentDetailsList) {
                List<AppointmentRequestedServices> appointmentRequestedServices = appointmentDetail.getAppointmentRequestedServices();
                for (AppointmentRequestedServices appointmentRequestedService : appointmentRequestedServices) {
                    if (serviceId.equals(appointmentRequestedService.getServices().getId()) &&
                            appointmentDetail.getState().equals(AppointmentState.COMPLETED)) {
                        appointmentCount++;
                        totalAppointments++;
                    }

                }
            }

            List<ProServiceDocuments> proDbDocuments = proServiceDocumentsRepository.findByProDetails_IdAndService_IdAndActiveStatusNotIn(proId, serviceId, Arrays.asList(0));
            List<ProServiceDocumentDetailsDTO> proDocuments = new ProServiceDocumentsConverter().convert(proDbDocuments);
            providedServiceDetails.add(
                    new ProvidedServiceDetailsDTO(serviceId, serviceIdToNameMap.get(serviceId),
                            serviceIdToIconMap.get(serviceId), appointmentCount, proService.getActiveStatus(), proDocuments,
                            proService.isCertified(), proService.isPrajekproVerified())
            );
            appointmentCount = 0l;
        }

        return providedServiceDetails;
    }

    private class ProServiceDocumentsConverter extends DTOConverter<ProServiceDocuments, ProServiceDocumentDetailsDTO> {

        @Override
        public ProServiceDocumentDetailsDTO convert(ProServiceDocuments input) {

            return dtoService.createProServiceDocumentDetailsDTO(input);
        }
    }

    @Override
    public BaseWrapper getProAppointments(Long proId, Pageable pageable) throws ServicesException {
        if (!hasValue(proId) || proId == 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        // List<Integer> state = Arrays.asList(AppointmentState.BOOKED.value(),AppointmentState.CONFIRMED.value(),AppointmentState.CHECKED_IN.value(),AppointmentState.CHECKED_OUT.value());
        List<AppointmentState> state = Arrays.asList(AppointmentState.BOOKED, AppointmentState.CONFIRMED, AppointmentState.CHECKED_IN, AppointmentState.CHECKED_OUT);
        List<AppointmentDetails> appointmentDetailsList = appointmentDetailsRepository.findByBookedFor_IdAndStateIn(proId, state, pageable);
        List<ProAppointmentDTO> proAppointmentList = new ArrayList<>();

        if (appointmentDetailsList.isEmpty()) {
            return new BaseWrapper(proAppointmentList);
        }

        for (AppointmentDetails details : appointmentDetailsList) {
            proAppointmentList.add(new ProAppointmentDTO(details));
        }
        Pagination pagination = new Pagination(proAppointmentList, proAppointmentList.size(), pageable);
        return new BaseWrapper(proAppointmentList, pagination);
    }

    @Override
    public BaseWrapper getProReview(Long proId, Pageable pageable) {
        if (pageable.isPaged()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("modifiedTs").descending());
        } else {
            pageable = PageRequest.of(0, 10, Sort.by("modifiedTs").descending());
        }

        List<ProReviews> proReviews = proReviewsRepository.findByProDetails_Id(proId, pageable);

        List<ProReviewsDTO> proReviewsDTOS = new ArrayList<>();

        if (!proReviews.isEmpty()) {
            for (ProReviews reviews : proReviews) {
                proReviewsDTOS.add(new ProReviewsDTO(reviews));
            }
        }
        Pagination pagination = new Pagination(proReviewsDTOS, proReviewsDTOS.size(), pageable);
        return new BaseWrapper(proReviewsDTOS, pagination);
    }

    @Override
    public BaseWrapper getProAppointmentsHistory(Long proId, Pageable pageable) throws ServicesException {

        if (!hasValue(proId) || proId == 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        // List<Integer> state = Arrays.asList(AppointmentState.BOOKED.value(),AppointmentState.CONFIRMED.value(),AppointmentState.CHECKED_IN.value(),AppointmentState.CHECKED_OUT.value());
        List<AppointmentState> state = Arrays.asList(AppointmentState.CANCELLED, AppointmentState.COMPLETED);
        List<AppointmentDetails> appointmentDetailsList = appointmentDetailsRepository.findByBookedFor_IdAndStateIn(proId, state, pageable);
        List<ProAppointmentDTO> proAppointmentList = new ArrayList<>();

        if (appointmentDetailsList.isEmpty()) {
            return new BaseWrapper(proAppointmentList);
        }

        for (AppointmentDetails details : appointmentDetailsList) {
            proAppointmentList.add(new ProAppointmentDTO(details));
        }

        Pagination pagination = new Pagination(proAppointmentList, proAppointmentList.size(), pageable);
        return new BaseWrapper(proAppointmentList, pagination);
    }

    @Override
    public BaseWrapper getProDeactivated(Long proId, StatusRemarkDTO statusRemark) throws ServicesException {
        if (!hasValue(proId) || proId == 0 || !hasValue(statusRemark)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Optional<ProDetails> proDetailsOptional = proDetailsRepository.findById(proId);

        if (!proDetailsOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        ProDetails proDetails = proDetailsOptional.get();
        Users user = proDetails.getUserDetails();
        ActiveStatus existedStatus = ActiveStatus.valueOf(user.getActiveStatus()).get();

        switch (statusRemark.getActiveStatus()) {
            case INACTIVE:
                user.updateAuditableFields(false, user.getEmailId(), ActiveStatus.INACTIVE.value());
                // user.setActiveStatus(ActiveStatus.INACTIVE.value());
                break;
            case ACTIVE:
                // user.setActiveStatus(ActiveStatus.ACTIVE.value());
                user.updateAuditableFields(false, user.getEmailId(), ActiveStatus.ACTIVE.value());
                user.setStatusRemark(null);
                break;
            case REGISTRATION_INITIATED:
                //user.setActiveStatus(ActiveStatus.REGISTRATION_INITIATED.value());
                user.updateAuditableFields(false, user.getEmailId(), ActiveStatus.REGISTRATION_INITIATED.value());
                break;
            case APPROVAL_PENDING:
                // user.setActiveStatus(ActiveStatus.APPROVAL_PENDING.value());
                user.updateAuditableFields(false, user.getEmailId(), ActiveStatus.APPROVAL_PENDING.value());
                break;
            case REASSESSMENT:
                // user.setActiveStatus(ActiveStatus.REASSESSMENT.value());
                if (!hasValue(statusRemark.getStatusRemark())) {
                    throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
                }
                user.updateAuditableFields(false, user.getEmailId(), ActiveStatus.REASSESSMENT.value());
                user.setStatusRemark(statusRemark.getStatusRemark());
                break;
            case ARCHIEVED:
                // user.setActiveStatus(ActiveStatus.ARCHIEVED.value());
                user.updateAuditableFields(false, user.getEmailId(), ActiveStatus.ARCHIEVED.value());
                break;
           /* default:
                user.setActiveStatus(ActiveStatus.INACTIVE.value());
                break;*/
        }

        usersRepository.save(user);

        String updateProStatusMessageNotification = String.format(updateProStatus, existedStatus.name(), statusRemark.getActiveStatus().name());
        sendNotification(user, "Registration Status Update", updateProStatusMessageNotification);
        return new BaseWrapper(proId);
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
    public BaseWrapper storeReviews(ProReviewsDTO request) throws ServicesException {

        if (!hasValue(request)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Users user = authorizationService.fetchLoggedInUser();
        Long proId = request.getProId();
        Optional<ProDetails> proDetailsOptional = proDetailsRepository.findById(proId);
        if (!proDetailsOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }
        ProDetails proDetails = proDetailsOptional.get();
        ProReviews proReviews = new ProReviews();
        proReviews.setReview(request.getReview());
        proReviews.setProDetails(proDetails);
        proReviews.setCustomer(user);
        proReviews.setAppointmentId(request.getAppointmentId());
        proReviews.setStarRating(request.getStarRating());
        proReviews.updateAuditableFields(true, user.getEmailId(), ActiveStatus.ACTIVE.value());

        proReviewsRepository.save(proReviews);

        List<ProReviews> proReviewsList = proReviewsRepository.findByProDetails_Id(proId);
        List<Float> ratingsList = proReviewsList.stream().map(ProReviews::getStarRating).collect(Collectors.toList());
        Float avgRating = 0f;
        for (Float rate : ratingsList) {
            avgRating = avgRating + rate;
        }
        avgRating = avgRating / ratingsList.size();

        proDetails.setRatings(avgRating.longValue());
        proDetails.setRatedBy((long) ratingsList.size());

        proDetailsRepository.save(proDetails);

        return new BaseWrapper(request);
    }

    @Override
    public BaseWrapper updateProAvailabilityStatus(AvailabilityStatus status) {

        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());

        proDetails.setAvailabilityStatus(status.value());
        proDetails.updateAuditableFields(false, user.getEmailId(), ActiveStatus.ACTIVE.value());

        proDetailsRepository.save(proDetails);
        return new BaseWrapper("Availability Status Updated");
    }
}
