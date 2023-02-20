package com.prajekpro.api.service;

import com.adyen.model.checkout.*;
import com.adyen.service.exception.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.*;
import com.google.gson.*;
import com.prajekpro.api.converters.*;
import com.prajekpro.api.domain.Currency;
import com.prajekpro.api.domain.PaymentDetails;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.domain.specifications.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.exception.*;
import com.prajekpro.api.helpers.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.util.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.repository.*;
import com.safalyatech.common.utility.*;
import com.safalyatech.emailUtility.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.env.*;
import org.springframework.core.io.*;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;

import javax.transaction.*;
import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class AppointmentServiceImpl implements AppointmentService {

    @Value("${notification_confirmed_appointment}")
    String confirmedAppointmentMessage;
    @Value("${notification_book_appointment}")
    String bookAppointmentMessage;
    @Value("${notification_cancelled_appointment}")
    String cancelledAppointmentMessage;
    @Value("${notification_update_appointment_status}")
    String updateAppointmentStatusMessage;
    @Value("${notification_completed_appointment_status}")
    String completedAppointmentStatusMessage;
    @Value("${notification_reschedule_appointment}")
    String rescheduleAppointment;
    //    @Value("${payments.portal.enable}")
//    private boolean enableGCashPayments;
    @Value("${payments.gcash.successURL}")
    private String successUrl;
    @Value("${payments.gcash.failureURL}")
    private String failureUrl;

    @Autowired
    private CommonUtility commonUtility;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private AppointmentDetailsRepository appointmentDetailsRepository;
    @Autowired
    private AppointmentRequestedServicesRepository appointmentRequestedServicesRepository;
    @Autowired
    private AppointmentRequestedServiceCategoriesRepository appointmentRequestedServiceCategoriesRepository;
    @Autowired
    private AppointmentRequestedServiceSubCategoriesRepository appointmentRequestedServiceSubCategoriesRepository;
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private ProDetailsRepository proDetailsRepository;
    @Autowired
    private PushNotificationRepository pushNotificationRepository;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private TimeSlotsRepository appointmentTimeSlotsRepository;
    @Autowired
    private ProServiceItemsPricingRepository proServiceItemsPricingRepository;
    @Autowired
    private UserDeliveryAddressRepository userDeliveryAddressRepository;
    @Autowired
    private ProReviewsRepository proReviewsRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private ServiceItemCategoryRepository serviceItemCategoryRepository;
    @Autowired
    private ServiceItemSubCategoryRepository serviceItemSubCategoryRepository;
    @Autowired
    private ProAvailableDayRepository proAvailableDayRepository;
    @Autowired
    private ProServiceTimeSlotsRepository proServiceTimeSlotsRepository;
    @Autowired
    private ProCancellationTimeRepository proCancellationTimeRepository;
    @Autowired
    private AppointmentInvoiceRepository appointmentInvoiceRepository;
    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;
    @Autowired
    private AppointmentOtherServicesRepository appointmentOtherServicesRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Environment env;
    @Autowired
    private WalletDetailsRepository walletDetailsRepository;
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private WalletTransactionHistoryRepository walletTransactionHistoryRepository;
    @Autowired
    private WalletTopUpHistoryRepository walletTopUpHistoryRepository;
    @Autowired
    private PrajekproWalletDetailsRepository prajekproWalletDetailsRepository;
    @Autowired
    private ConfigurationRepository configurationRepository;
    @Autowired
    private TaxConfigRepository taxConfigRepository;
    @Autowired
    private ProServiceUnavailableDatesRepository proServiceUnavailableDatesRepository;
    @Autowired
    private ProServicesRepository proServicesRepository;

    @Autowired
    private PaymentsHelper paymentHelper;
    @Autowired
    private WalletHelper walletHelper;

    @Value("${file.upload.path}")
    private String fileUploadPath;
    @Value("${payments.gcash.returnUrl}")
    private String returnUrl;
    @Value("${payments.gcash.dummyCheckoutUrl}")
    private String dummyCheckoutUrl;
    @Value("${prowallet.locked.amt}")
    private Double proWalletLockedAmt;



    @Override
    public BaseWrapper getAppointmentTimeSlots(AppointmentSlotsDTO request, String timeZone) throws ServicesException, JsonParseException, JsonMappingException, IOException {
        Long serviceId = request.getServiceId();
        Long proId = request.getProId();
        String date = request.getDate();
        //TODO: Automate the request body validation flow
        if (!hasValue(request.getServiceId())
                || !hasValue(request.getProId()) || !hasValue(request.getDate()))
            throw new ServicesException("Invalid Request");
        log.debug("Time Slot demanded for date {} and vendor ID - {}", request.getDate(), request.getProId());

        LocalDate today = LocalDate.now(ZoneId.of(timeZone));
        LocalTime todayTime = LocalTime.now(ZoneId.of(timeZone));
        LocalDate appointmentDate = LocalDate.parse(date);
        // throw error to select past date
        if (appointmentDate.isBefore(today)) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        List<AppointmentState> appointmentState = Arrays.asList(AppointmentState.COMPLETED, AppointmentState.CANCELLED);
        //booked appointments time slot id's
        List<Long> bookedAppointmentTimeSlotIds = appointmentRequestedServicesRepository.getTimeSlotId(proId, date, appointmentState);
        // master time slots data
        List<TimeSlots> appointmentTimeSlotsList = appointmentTimeSlotsRepository.findByActiveStatus(ActiveStatus.ACTIVE.value());
        log.debug("booked Appointment time slots id = {}", bookedAppointmentTimeSlotIds);
        AppointmentTimeSlotsDTO appointmentTimeSlotsDTO;

        //Check for Unavailable Dates
        List<ProServiceUnavailableDates> proServiceUnavailableDates = proServiceUnavailableDatesRepository.findByProDetails_IdAndService_IdAndStartDtLessThanEqualAndEndDtGreaterThanEqualAndActiveStatusIn(proId, serviceId, date, date, Arrays.asList(ActiveStatus.ACTIVE.value()));
        System.out.println("proServiceUnavailableDates = " + proServiceUnavailableDates.toString());
        if (hasValue(proServiceUnavailableDates))
            return new BaseWrapper(new AppointmentTimeSlotsDTO(appointmentTimeSlotsList, request));

        String dayOfWeek = appointmentDate.getDayOfWeek().toString();
        log.debug("day of appointment date = {}", dayOfWeek);
        List<Long> proAvailableTimeSlotId = null;
        List<ProAvailableDay> proAvailableDays = proAvailableDayRepository.fetchDaysByProAndService(proId, serviceId);
        for (ProAvailableDay availableDay : proAvailableDays) {
            if (dayOfWeek.equalsIgnoreCase(availableDay.getAvailableDays().getValue())) {
                proAvailableTimeSlotId = proServiceTimeSlotsRepository.fetchByProIdAndServiceId(proId, serviceId);
            }
        }
        log.debug("pro available time slot id = {}", proAvailableTimeSlotId);

        if (proAvailableTimeSlotId == null) {

            log.debug("when pro is unAvailable");
            appointmentTimeSlotsDTO = new AppointmentTimeSlotsDTO(appointmentTimeSlotsList, request);

        } else {

            log.debug("when pro is available");
            appointmentTimeSlotsDTO = new AppointmentTimeSlotsDTO(bookedAppointmentTimeSlotIds, appointmentTimeSlotsList, request, timeZone, proAvailableTimeSlotId);
        }

        return new BaseWrapper(appointmentTimeSlotsDTO);
    }

    //book Appointment
    @Override
    public BaseWrapper bookAppointment(AppointmentBookingDTO request) throws ServicesException, ParseException {

        log.info("request = {}", request.toString());
        Long bookedFor = request.getProId();
        //TODO:check for the date and time
        List<AppointmentServicesDTO> appointmentServicesList = request.getAppointmentServices();

        getDuplicateRecords(appointmentServicesList.get(0), bookedFor);

        AppointmentDetails appointmentDetails = new AppointmentDetails();

        appointmentDetails.setBookedBy(authorizationService.fetchLoggedInUser());
        Optional<ProDetails> proDetailsOptional = proDetailsRepository.findById(request.getProId());
        ProDetails proDetails = null;
        if (proDetailsOptional.isPresent()) {
            proDetails = proDetailsOptional.get();
        }
        appointmentDetails.setBookedFor(proDetails);

        if (!hasValue(request.getUserAddressDetailsId())) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Optional<UserDeliveryAddress> userDeliveryAddressOptional = userDeliveryAddressRepository.findById(request.getUserAddressDetailsId());

        if (!userDeliveryAddressOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        UserDeliveryAddress userDeliveryAddress = userDeliveryAddressOptional.get();
        appointmentDetails.setUserDeliveryAddress(userDeliveryAddress);

        appointmentDetails.setState(AppointmentState.BOOKED);

        appointmentDetails.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        List<AppointmentRequestedServices> appointmentRequestedServices = new ArrayList<>();

        Double subTotal = 0.0d;
        String appointmentDateTime = "";
        for (AppointmentServicesDTO service : request.getAppointmentServices()) {

            log.debug("AppointmentRequestedService Id = {}", service.getServiceId());
            AppointmentRequestedServices serviceWithUpdateFields = new AppointmentRequestedServices(service, appointmentDetails);
            serviceWithUpdateFields.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            appointmentDateTime = String.format("%s %s", serviceWithUpdateFields.getAppointmentDate(), serviceWithUpdateFields.getAppointmentTime());

            List<AppointmentRequestedServiceCategories> appointmentRequestedServiceCategories = new ArrayList<>();

            for (AppointmentServiceCategoryDTO serviceCategory : service.getAppointmentServiceCategory()) {

                AppointmentRequestedServiceCategories category = new AppointmentRequestedServiceCategories(serviceCategory, serviceWithUpdateFields);
                category.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

                List<AppointmentRequestedServiceSubCategories> appointmentRequestedServiceSubCategories = new ArrayList<>();

                for (AppointmentServiceSubCategoryDTO serviceSubCategory : serviceCategory.getAppointmentServiceSubCategory()) {
                    subTotal = subTotal + ((serviceSubCategory.getReqPrice()) * (serviceSubCategory.getReqQty()));
                    log.info("serviceSubCategory id = {}", serviceSubCategory.getServiceSubCategoryId());
                    AppointmentRequestedServiceSubCategories subCategory = new AppointmentRequestedServiceSubCategories(serviceSubCategory, category);
                    subCategory.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
                    appointmentRequestedServiceSubCategories.add(subCategory);
                }
                category.setAppointmentRequestedServiceSubCategories(appointmentRequestedServiceSubCategories);
                appointmentRequestedServiceCategories.add(category);
            }
            serviceWithUpdateFields.setAppointmentRequestedServiceCategories(appointmentRequestedServiceCategories);
            appointmentRequestedServices.add(serviceWithUpdateFields);
        }
        appointmentDetails.setAppointmentRequestedServices(appointmentRequestedServices);

        appointmentDetails.setSubTotal(subTotal);
        appointmentDetails.setGrandTotal(request.getGrandTotal());
        List<AppointmentTaxDetails> appointmentTaxDetailsList = getAppointmentTaxDetailsList(request.getApplicableTaxes(), appointmentDetails);
        appointmentDetails.setAppointmentTaxDetailsList(appointmentTaxDetailsList);

        appointmentDetailsRepository.save(appointmentDetails);

        //send notification to pro
//        String appointmentDateTime = (new DateUtil()).milliSecondToDate(appointmentDetails.getCreatedTs());
        String appointmentDateTimeForNotification = (new DateUtil()).convertDateFormat(appointmentDateTime);
        String bookAppointmentMessageNotification = String.format(bookAppointmentMessage, appointmentDateTimeForNotification);
        sendNotification(appointmentDetails.getBookedFor().getUserDetails(), AppointmentNotificationEnum.APPOINTMENT_BOOKED.value(),
                bookAppointmentMessageNotification, authorizationService.fetchLoggedInUser());
        return new BaseWrapper(appointmentDetails.getId());
    }

    private List<AppointmentTaxDetails> getAppointmentTaxDetailsList(List<TaxConfigDTO> request, AppointmentDetails appointmentDetails) throws ServicesException {
        List<AppointmentTaxDetails> appointmentTaxDetailsList = new ArrayList<>();
        if (request.isEmpty()) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        for (TaxConfigDTO taxDto : request) {
            AppointmentTaxDetails appointmentTaxDetails = new AppointmentTaxDetails(taxDto, appointmentDetails);
            if (hasValue(taxDto.getAppointmentTaxId()) && taxDto.getAppointmentTaxId() > 0) {
                appointmentTaxDetails.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            } else {
                appointmentTaxDetails.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            }
            appointmentTaxDetailsList.add(appointmentTaxDetails);
        }

        return appointmentTaxDetailsList;
    }

    private void sendNotification(Users notificationToUser, String notificationTitle, String notificationBody, Users loggedInUser) {

        List<String> tokenListByUserId = pushNotificationRepository.findTokenListByUserId(notificationToUser.getUserId());
        if (!tokenListByUserId.isEmpty()) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest(
                    notificationTitle, notificationBody, tokenListByUserId, NotificationType.APPOINTMENT);

            //save notification to DB
            UserNotificationMetadataDTO userNotificationMetadataDTO = new UserNotificationMetadataDTO(pushNotificationRequest.getMessage(), pushNotificationRequest.getNotificationTime());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonMetadata = gson.toJson(userNotificationMetadataDTO);

            UserNotification userNotification = new UserNotification(notificationToUser, pushNotificationRequest.getTitle(), NotificationType.APPOINTMENT,
                    jsonMetadata, false, false);
            userNotification.updateAuditableFields(true, loggedInUser.getEmailId(), ActiveStatus.ACTIVE.value());
            userNotificationRepository.save(userNotification);

            //cal Async to send notification
            pushNotificationService.sendPushNotificationToMultipleToken(getPayloadDataForNotification(loggedInUser),
                    pushNotificationRequest);
        }
    }

    private Map<String, String> getPayloadDataForNotification(Users loggedInUser) {
        HashMap<String, String> data = new HashMap<>();
        data.put("userId", loggedInUser.getUserId());
        return data;
    }

    //List Of Appointment
    @Override
    public BaseWrapper getListOfAppointment(String timeZone, Pageable pageable, AppointmentState status) {
        String userId = authorizationService.fetchLoggedInUser().getUserId();

        List<AppointmentDTO> currentAppointmentList = new ArrayList<>();
        List<AppointmentDTO> pastAppointmentList = new ArrayList<>();

        List<Integer> currentState = Arrays.asList(AppointmentState.BOOKED.value(), AppointmentState.CONFIRMED.value(), AppointmentState.CHECKED_IN.value(), AppointmentState.CHECKED_OUT.value());
        List<Integer> pastState = status != AppointmentState.ALL ? Arrays.asList(status.value()) : Arrays.asList(AppointmentState.COMPLETED.value(), AppointmentState.CANCELLED.value());

        if (status != AppointmentState.ALL) {
            if (currentState.contains(status.value())) {
                currentState = Arrays.asList(status.value());
                pastState = new ArrayList<>(0);
            } else {
                pastState = Arrays.asList(status.value());
                currentState = new ArrayList<>(0);
            }
        }

        LocalDate currentDate = LocalDate.now(ZoneId.of(timeZone));
        log.debug("today's date = {}", currentDate);

        //TODO:Optimize the code
        List<AppointmentDetails> currentAppointment;
        if (hasValue(currentState))
            currentAppointment = appointmentDetailsRepository.findByBookedByAndStateCurrentAppointment(userId, currentState, currentDate, pageable);
        else
            currentAppointment = new ArrayList<>(0);

        List<AppointmentDetails> pastAppointment;
        System.out.println("userId - " + userId + ", pastState - " + pastState.toString());
        if (hasValue(pastState))
            pastAppointment = appointmentDetailsRepository.findByBookedByAndStatePastAppointment(userId, pastState, pageable);
        else
            pastAppointment = new ArrayList<>(0);

        log.debug("currentAppointment = {}", currentAppointment.isEmpty());

        for (AppointmentDetails appointmentDetails : currentAppointment) {
            AppointmentDTO appointment = new AppointmentDTO(appointmentDetails);
            PaymentDTO payment = getPaymentDetails(appointmentDetails.getId());
            if (hasValue(payment)) {
                appointment.setPayment(payment);
            }
            currentAppointmentList.add(appointment);
        }

        for (AppointmentDetails appointmentDetails : pastAppointment) {
            AppointmentDTO appointment = new AppointmentDTO(appointmentDetails);
            PaymentDTO payment = getPaymentDetails(appointmentDetails.getId());
            if (hasValue(payment)) {
                appointment.setPayment(payment);
            }
            pastAppointmentList.add(appointment);
        }

        AppointmentListDTO appointmentList = new AppointmentListDTO(currentAppointmentList, pastAppointmentList);
        return new BaseWrapper(appointmentList);
    }

    private PaymentDTO getPaymentDetails(Long id) {
        PaymentDetails paymentDetails = paymentDetailsRepository.getPaymentDetailsByAppointmentId(id);
        if (hasValue(paymentDetails)) {
            return new PaymentDTO(paymentDetails);
        }
        return null;
    }

    //Appointment Details
    @Override
    public BaseWrapper getAppointmentDetails(Long id) throws ServicesException {

        AppointmentDetails appointmentDetails = validateAppointmentId(id);

        AppointmentBookingDTO appointmentBookingDetails = new AppointmentBookingDTO(appointmentDetails);

        // cancellation time and fees for pro n service
        Long proId = appointmentDetails.getBookedFor().getId();
        List<AppointmentRequestedServices> requestedServices = appointmentDetails.getAppointmentRequestedServices();
        for (AppointmentRequestedServices service : requestedServices) {
            //proCancellationConfigDetails index details
            //0 = Cancellation Time
            //1 = Cancellation Time Unit
            //2 = Cancellation Time Unit ID
            Object[] proCancellationConfigDetails = getProCancellationConfigDetails(proId, service);

            //Update Cancellation time, time unit and time unit ID
            appointmentBookingDetails.setCancellationTime((Long) proCancellationConfigDetails[0]);
            appointmentBookingDetails.setCancellationTimeUnit(proCancellationConfigDetails[1].toString());
            appointmentBookingDetails.setCancellationTimeUnitId((Long) proCancellationConfigDetails[2]);

            appointmentBookingDetails.setCancellationFees(service.getServices().getCancellationFees());
            appointmentBookingDetails.setCancellationFeesUnit(service.getServices().getCancellationFeesUnit().getValue());
            appointmentBookingDetails.setCancellationFeesUnitId(service.getServices().getCancellationFeesUnit().getId());

            appointmentBookingDetails.setGrandTotal(appointmentDetails.getGrandTotal());
        }

        //Add applicable taxes in appointment details response
        List<AppointmentTaxDetails> appointmentTaxDetailsList = appointmentDetails.getAppointmentTaxDetailsList();
        if (!appointmentTaxDetailsList.isEmpty()) {
            List<TaxConfigDTO> taxConfigDTOList = new ArrayList<>();
            for (AppointmentTaxDetails taxDetails : appointmentTaxDetailsList) {
                TaxConfigDTO taxDTO = new TaxConfigDTO(taxDetails);
                taxConfigDTOList.add(taxDTO);
            }
            appointmentBookingDetails.setApplicableTaxes(taxConfigDTOList);
        }

        List<ApptDocs> apptDocsList = apptDocsRepository.findByAppointmentDetails_Id(id);
        appointmentBookingDetails.setApptDocs(new ApptDocDTOConverter().convert(apptDocsList));

        return new BaseWrapper(appointmentBookingDetails);
    }

    private class ApptDocDTOConverter extends DTOConverter<ApptDocs, ApptDocsDTO> {
        @Override
        public ApptDocsDTO convert(ApptDocs input) {
            return new ApptDocsDTO(input);
        }
    }

    private Object[] getProCancellationConfigDetails(Long proId, AppointmentRequestedServices service) {
        Long serviceId = service.getServices().getId();

        ProCancellationTime proCancellationTime = proCancellationTimeRepository.fetchTimeByProIdAndServiceId(proId, serviceId);

        //Set Config value based on if PRO has configured any
        //proCancellationConfigDetails index details
        //0 = Cancellation Time
        //1 = Cancellation Time Unit
        //2 = Cancellation Time Unit ID
        Object[] proCancellationConfigDetails = new Object[3];
        if (hasValue(proCancellationTime)) {
            proCancellationConfigDetails[0] = proCancellationTime.getCancellationTime();
            proCancellationConfigDetails[1] = proCancellationTime.getCancellationTimeUnit().getValue();
            proCancellationConfigDetails[2] = proCancellationTime.getCancellationTimeUnit().getId();
        } else {
            proCancellationConfigDetails[0] = service.getServices().getCancellationTime();
            proCancellationConfigDetails[1] = service.getServices().getCancellationTimeUnit().getValue();
            proCancellationConfigDetails[2] = service.getServices().getCancellationTimeUnit().getId();
        }

        return proCancellationConfigDetails;
    }

    /**
     * Update Appointment State
     */
    @Override
    public BaseWrapper updateAppointmentState(Long appointmentId, AppointmentState state,
                                              RemarksDTO cancellationRemarks, Long appointmentRequestedServiceId,
                                              Users loggedInUser) throws ServicesException, ParseException {

        if (!hasValue(appointmentId) || appointmentId <= 0 ||
                !hasValue(state) || !hasValue(appointmentRequestedServiceId) || appointmentRequestedServiceId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        //Get Appt. Details
        AppointmentDetails appointmentDetails = validateAppointmentId(appointmentId);

        Optional<AppointmentRequestedServices> appointmentRequestedServicesOptional = appointmentRequestedServicesRepository.findById(appointmentRequestedServiceId);
        if (!appointmentRequestedServicesOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        //condition for update Check_in time and Check_out time
        AppointmentRequestedServices requestedService = appointmentRequestedServicesOptional.get();

        //Update Check-In time if state=CHECKED_IN
        if (state.name().equals(AppointmentState.CHECKED_IN.name())) {
            LocalTime time = LocalTime.now();
            requestedService.setCheckInTime(time);
            appointmentRequestedServicesRepository.save(requestedService);
        }

        //Update Check-Out time if state=CHECKED_OUT
        if (state.name().equals(AppointmentState.CHECKED_OUT.name())) {
            LocalTime time = LocalTime.now();
            requestedService.setCheckOutTime(time);
            appointmentRequestedServicesRepository.save(requestedService);
        }

        // condition for update remarks and the cancelledBy field
        if (state.name().equals(AppointmentState.CANCELLED.name())) {
            String cancelledBy = loggedInUser.getUserId();
            appointmentDetails.setCancelledBy(cancelledBy);

            if (appointmentDetails.getState() != AppointmentState.BOOKED) {
                Set<RoleMasterDtl> roles = loggedInUser.getRoles();
                for (RoleMasterDtl roleMasterDtl : roles) {

                    System.out.println("Role In Iteration: " + roleMasterDtl.getRoles() + ", And is vendor/default user: " + ((roleMasterDtl.getRoles()).equals(Roles.ROLE_VENDOR.name()) || (roleMasterDtl.getRoles()).equals(Roles.ROLE_DEFAULT_USER.name())));
                    //If Appt. is cancelled by PRO, then cut the penalty amount for cancellation and add it to PrajakePro Wallet
                    if ((roleMasterDtl.getRoles()).equals(Roles.ROLE_VENDOR.name())
                            || (roleMasterDtl.getRoles()).equals(Roles.ROLE_DEFAULT_USER.name())) {
                        //Check if a cancellation remark is provided
                        if (null == cancellationRemarks.getRemarkId()) {
                            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
                        } else {
                            PPLookUp lookUp = new PPLookUp(cancellationRemarks.getRemarkId());
                            appointmentDetails.setCancelledRemark(lookUp);
                            appointmentDetails.setCancelledRemarksDesc(cancellationRemarks.getRemarks());
                        }

                        //Release appointment commission amount to PRO wallet
                        walletHelper.releaseApptCommissionAmtToProWallet(appointmentDetails);

                        //Deduct Appointment cancellation penalty amount from PRO locked amount and add to PrajekPro Wallet
                        Double apptCancellationPnltyAmt = appointmentDetails.getPrajekProLockedAmount();

                        //Create an active payment details record for Prajekpro top up with Cancellation Penalty
                        int transactionId = walletHelper.generateTransactionId();
                        TransactionType transactionType = TransactionType.APPOINTMENT_CANCEL;
                        ProDetails proDetails = appointmentDetails.getBookedFor();
                        PaymentDetails paymentDetails = new PaymentDetails(proDetails, GlobalConstants.PaymentMethods.PAYPAL, PaymentStatus.COMPLETED.name(),
                                transactionId, transactionType, PaymentsResponse.ResultCodeEnum.AUTHORISED.name(), true, GlobalConstants.USER_API,
                                ActiveStatus.ACTIVE.value(), null);
                        paymentDetailsRepository.save(paymentDetails);

                        //Create a PRO Wallet transaction record for deduction of cancellation penalty from already locked amount
                        WalletTransactionHistory walletTransactionHistory = walletHelper.addProWalletTransactionHistory(proDetails, paymentDetails,
                                transactionId, apptCancellationPnltyAmt, transactionType, appointmentDetails, cancelledBy, ActiveStatus.ACTIVE.value());

                        //Add Cancellation Penalty paid by PRO to PrajekPro Wallet
                        walletHelper.addPrajekProWalletTransaction(apptCancellationPnltyAmt, WalletAmountType.CANCELLATION_PENALTY, ActiveStatus.ACTIVE.value(),
                                appointmentDetails.getBookedFor(), appointmentDetails, walletTransactionHistory);

                        //Release cancellation penalty from PRO wallet locked amount
                        walletHelper.deductApptCancellationPenaltyAmtFromProLockedAmt(appointmentDetails);
                    }

                    //If Appt. is cancelled by Customer, then refund all locked amount to Pro Wallet
                    if ((roleMasterDtl.getRoles()).equals(Roles.ROLE_CUSTOMER.name())) {
                        walletHelper.releaseAllApptLockedAmtToProWallet(appointmentDetails);
                        break;
                    }
                }
            }
        }

        //Once Appt. is confirmed then lock the commission amount of Appointment for pro
        // This implemetation flow is disabled for first 6 months.

       /* if (state.name().equals(AppointmentState.CONFIRMED.name())) {
            //Get PRO Wallet details
            ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(
                    appointmentDetails.getBookedFor().getId(), ActiveStatus.ACTIVE.value());

            List<String> configName = Arrays.asList(ConfigEnum.APPOINTMENT_PRO_CNCL_PNLTY_PERCENTAGE.name(), ConfigEnum.APPOINTMENT_COMPLETION_PRAJEKPRO_PERCENT.name()
                    , ConfigEnum.WALLET_DEFAULT_LIMIT.name());
            List<Configuration> configuration = configurationRepository.findByConfigNameIn(configName);

            Float proCnclPnltyPercent = 0.0f;
            Float prajekProPercent = 0.0f;
            Float walletDefaultLimit = 0.0f;
            for (Configuration config : configuration) {
                if (config.getConfigName().equals(ConfigEnum.APPOINTMENT_PRO_CNCL_PNLTY_PERCENTAGE.name())) {
                    proCnclPnltyPercent = Float.valueOf(config.getConfigValue());
                }
                if (config.getConfigName().equals(ConfigEnum.APPOINTMENT_COMPLETION_PRAJEKPRO_PERCENT.name())) {
                    prajekProPercent = Float.valueOf(config.getConfigValue());
                }
                if (config.getConfigName().equals(ConfigEnum.WALLET_DEFAULT_LIMIT.name())) {
                    walletDefaultLimit = Float.valueOf(config.getConfigValue());
                }
            }

            Double totalCost = appointmentDetails.getSubTotal();
            Double cnclPnltyLockedAmount = totalCost * (proCnclPnltyPercent / 100f);
            Double prajekProLockedAmount = totalCost * (prajekProPercent / 100f);
            Double lockedAmount = cnclPnltyLockedAmount + prajekProLockedAmount;

            Double walletAmount = proWalletDetails.getAmount();
            walletAmount = walletAmount - lockedAmount;

            if (walletAmount < walletDefaultLimit)
                throw new ServicesException(GeneralErrorCodes.ERR_INSUFFICIENT_BALANCE_IN_WALLET.value());

            proWalletDetails.setAmount(walletAmount);
            if (hasValue(proWalletDetails.getLockedAmount())) {
                lockedAmount = proWalletDetails.getLockedAmount() + lockedAmount;
            }
            proWalletDetails.setLockedAmount(lockedAmount);
            walletDetailsRepository.save(proWalletDetails);

            appointmentDetails.setCancellationPnltyLockedAmount(cnclPnltyLockedAmount);
            appointmentDetails.setPrajekProLockedAmount(prajekProLockedAmount);
        }*/

        if (state.name().equals(AppointmentState.CONFIRMED.name())) {
            //Get PRO Wallet details
            ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(
                    appointmentDetails.getBookedFor().getId(), ActiveStatus.ACTIVE.value());


            Double totalCost = appointmentDetails.getSubTotal();
            Double serviceLockedAmount = totalCost * (10 / 100f);
            Double walletAmount = proWalletDetails.getAmount();

            if (walletAmount < serviceLockedAmount)
                throw new ServicesException(GeneralErrorCodes.ERR_INSUFFICIENT_BALANCE_IN_WALLET.value());

            proWalletDetails.setLockedAmount(serviceLockedAmount);
            walletDetailsRepository.save(proWalletDetails);
            appointmentDetails.setPrajekProLockedAmount(serviceLockedAmount);
        }

        //Update Appointment State
        appointmentDetails.setState(state);
        appointmentDetailsRepository.save(appointmentDetails);

        //Notification to pro/user
        String appointmentDateTime = String.format("%s %s", requestedService.getAppointmentDate(), requestedService.getAppointmentTime());
        sendNotificationForAppointmentStatus(appointmentDetails, appointmentDetails.getBookedBy(),
                appointmentDetails.getBookedFor().getUserDetails(), appointmentDateTime, loggedInUser);

        return new BaseWrapper(appointmentDetails.getId());
    }



    @Async
    private void sendNotificationForAppointmentStatus(AppointmentDetails appointmentDetails, Users customer, Users pro, String appointmentDateTime, Users loggedInUser) throws ParseException {
        Users notificationToUser = null;
        String notificationBody = "";

//        String appointmentDateTime = (new DateUtil()).milliSecondToDate(appointmentDetails.getCreatedTs());
        String appointmentDateTimeForNotification = (new DateUtil()).convertDateFormat(appointmentDateTime);
        if (AppointmentState.CONFIRMED.name().equalsIgnoreCase(appointmentDetails.getState().name())) {
            notificationToUser = customer;
            notificationBody = String.format(confirmedAppointmentMessage, appointmentDateTimeForNotification, loggedInUser.getFullName());
        } else if (AppointmentState.CHECKED_IN.name().equals(appointmentDetails.getState().name())) {
            notificationToUser = customer;
            notificationBody = String.format(updateAppointmentStatusMessage, appointmentDateTimeForNotification, "Checked In", loggedInUser.getFullName());
        } else if (AppointmentState.CHECKED_OUT.name().equals(appointmentDetails.getState().name())) {
            notificationToUser = customer;
            notificationBody = String.format(updateAppointmentStatusMessage, appointmentDateTimeForNotification, "Checked Out", loggedInUser.getFullName());
        } else if (AppointmentState.COMPLETED.name().equals(appointmentDetails.getState().name())) {
            notificationToUser = customer;
            notificationBody = String.format(completedAppointmentStatusMessage, appointmentDateTimeForNotification, loggedInUser.getFullName());
        } else if (AppointmentState.CANCELLED.name().equals(appointmentDetails.getState().name())) {
            if (null == authorizationService.fetchLoggedInUser()
                    || authorizationService.fetchLoggedInUser().getUserId().equals(customer.getUserId()))
                notificationToUser = pro;
            else notificationToUser = customer;
            notificationBody = String.format(cancelledAppointmentMessage, appointmentDateTimeForNotification, loggedInUser.getFullName());
        }

        sendNotification(notificationToUser, AppointmentNotificationEnum.APPOINTMENT_UPDATED.value(), notificationBody, loggedInUser);
    }

    //Re_schedule the Appointment
    @Override
    public BaseWrapper reScheduleAppointment(AppointmentServicesDTO appointmentServices, Long appointmentId) throws ServicesException, ParseException {

        Optional<AppointmentRequestedServices> appointmentRequestedServicesOptional = appointmentRequestedServicesRepository.findById(appointmentServices.getAppointmentRequestedServiceId());
        if (!appointmentRequestedServicesOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

        AppointmentRequestedServices appointmentRequestedServices = appointmentRequestedServicesOptional.get();

        log.info("Existing Appt. Date = {} and Time Slot ID = {}", appointmentRequestedServices.getAppointmentDate(), appointmentRequestedServices.getTimeSlotId());
        log.info("Request Appt. Date = {} and Time Slot ID = {}", appointmentServices.getDate(), appointmentServices.getTimeSlot().getId());
        log.info("Is Appt. Date/Time Different = {}", (!appointmentRequestedServices.getAppointmentDate().equals(appointmentServices.getDate())
                || !appointmentRequestedServices.getTimeSlotId().equals(appointmentServices.getTimeSlot().getId())));
        if (!appointmentRequestedServices.getAppointmentDate().equals(appointmentServices.getDate())
                || !appointmentRequestedServices.getTimeSlotId().equals(appointmentServices.getTimeSlot().getId())) {
            Long bookedFor = appointmentRequestedServices.getAppointmentDetails().getBookedFor().getId();

            getDuplicateRecords(appointmentServices, bookedFor);

            appointmentRequestedServices.setAppointmentTime(appointmentServices.getTimeSlot().getValue());
            appointmentRequestedServices.setTimeSlotId(appointmentServices.getTimeSlot().getId());
            appointmentRequestedServices.setAppointmentDate(appointmentServices.getDate());
            appointmentRequestedServicesRepository.save(appointmentRequestedServices);

            Optional<AppointmentDetails> appointmentDetailsOptional = appointmentDetailsRepository.findById(appointmentId);
            if (!appointmentDetailsOptional.isPresent()) {
                throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
            }
            AppointmentDetails appointmentDetails = appointmentDetailsOptional.get();
            String appointmentDateTime = String.format("%s %s", appointmentServices.getDate(), appointmentServices.getTimeSlot().getValue());
            String appointmentDateTimeForNotification = (new DateUtil()).convertDateFormat(appointmentDateTime);
            String rescheduleAppointmentMessageNotification = String.format(rescheduleAppointment, appointmentDetails.getBookedBy().getFullName(), appointmentDateTimeForNotification);
            sendNotification(appointmentDetails.getBookedFor().getUserDetails(), AppointmentNotificationEnum.APPOINTMENT_UPDATED.value(),
                    rescheduleAppointmentMessageNotification, authorizationService.fetchLoggedInUser());
        }

        return new BaseWrapper(appointmentServices);
    }

    @Override
    public BaseWrapper getProAppointmentList(Long serviceId, String term,
                                             String status, String timeZone, Pageable pageable, FilterParamDTO filterRequest) throws ServicesException, PPServicesException {

        String userId = authorizationService.fetchLoggedInUser().getUserId();

        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(userId);

        if (proDetails == null)
            throw new ServicesException(GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());

        Long proId = proDetails.getId();
        log.debug("proId ={}", proId);

        List<Integer> stateForCounting = Arrays.asList(AppointmentState.BOOKED.value());


        LocalDate currentDate = LocalDate.now(ZoneId.of(timeZone));
//        LocalDate currentDate = LocalDate.parse("2021-11-01");
        log.debug("today's date = {}", currentDate);

        LocalTime currentTime = LocalTime.now(ZoneId.of(timeZone));
        log.debug("Today time = {}", currentTime);

        log.debug("bookedFor: {}, state: {}, currDate: {} and currTime: {}", proId, stateForCounting.toString(), currentDate, currentTime);
        //set of services provided by the pro
//        Set<Services> proServices = servicesRepository.fetchServicesByProIdIn(proId, Arrays.asList(ActiveStatus.ACTIVE.value()));
        List<ProServices> proServices = proServicesRepository.fetchProActiveServiceByProId(proId, ActiveStatus.ACTIVE.value());
        List<ProvidedServiceDetailsDTO> providedServiceDetails = new ArrayList<>();


        boolean isTermPresent = hasValue(term);

        if (isTermPresent) {
            term = "%" + term.trim() + "%";
        }

        //Create a Map of Service ID against Service Name
        Map<Long, String> serviceIdToNameMap = servicesRepository
                .findAllByActiveStatus(ActiveStatus.ACTIVE.value(), null)
                .get()
                .collect(Collectors.toMap(
                        Services::getId, Services::getServiceName
                ));
        System.out.println("serviceIdToNameMap = " + serviceIdToNameMap);
        for (ProServices proService : proServices) {
            if (proService.getActiveStatus() == ActiveStatus.ACTIVE.value()) {
                Long appointmentCount = 0l;
                Long serviceID = proService.getServiceId();
                if (!isTermPresent)
                    appointmentCount = appointmentDetailsRepository.countOfProAppointmentsByServiceAndState(proId, stateForCounting, currentDate, serviceID);
                else
                    appointmentCount = appointmentDetailsRepository.countOfProAppointmentsByServiceAndStateAndSearchTerm(proId, stateForCounting, currentDate, serviceID, term);
                providedServiceDetails.add(new ProvidedServiceDetailsDTO(serviceID, serviceIdToNameMap.get(serviceID), appointmentCount));
            }
        }

        // filtering for Appointments List
        List<Order> sortOrder = new ArrayList<>();
        //TODO: Take from UI
        pageable = PageRequest.of(0, 500, Sort.by(sortOrder));

        AppointmentState reqStatus = getStatusFromRequest(status);
        List<Integer> stateForListing;
        if (reqStatus == AppointmentState.ALL && !isTermPresent)
            stateForListing = Arrays.asList(AppointmentState.BOOKED.value(), AppointmentState.CONFIRMED.value(), AppointmentState.CHECKED_IN.value(), AppointmentState.CHECKED_OUT.value());
        else if (reqStatus == AppointmentState.ALL && isTermPresent)
            stateForListing = Arrays.asList(AppointmentState.CONFIRMED.value(), AppointmentState.CHECKED_IN.value(), AppointmentState.CHECKED_OUT.value());
        else
            stateForListing = Arrays.asList(reqStatus.value());
        System.out.println("stateForListing = " + stateForListing.toString());

        Page<AppointmentDetails> appointmentDetailsPage;
        if (!isTermPresent)
            appointmentDetailsPage = appointmentDetailsRepository.findByBookedForAndServiceId(proId, stateForListing, serviceId, currentDate, pageable);
        else {
            System.out.println("Here");
            appointmentDetailsPage = appointmentDetailsRepository.findByBookedForAndServiceIdAndSearchTerm(proId, stateForListing, serviceId, currentDate, term, pageable);
        }
        List<AppointmentDetails> appointmentDetailsList = appointmentDetailsPage.getContent();
        log.debug("proId = {}, stateForListing = {}, serviceId = {}, currentDate = {}, term = {}", proId, stateForListing, serviceId, currentDate, term);
        System.out.println("appointmentDetailsList size = " + appointmentDetailsList.size());

        List<AppointmentBookingDTO> appointmentBookingDTOList = new ArrayList<>();
        List<Long> appointmentBookingDTOIDList = new ArrayList<>();
        for (AppointmentDetails details : appointmentDetailsList) {
            if (!appointmentBookingDTOIDList.contains(details.getId())) {
                appointmentBookingDTOList.add(new AppointmentBookingDTO(details));
                appointmentBookingDTOIDList.add(details.getId());
            }
        }

        AppointmentDetailsMetaDataDTO appointmentDetailsMetaData = new AppointmentDetailsMetaDataDTO(serviceId, appointmentBookingDTOList);
        ProAppointmentDetailsDTO proAppointmentDetails = new ProAppointmentDetailsDTO(providedServiceDetails, appointmentDetailsMetaData);

        return new BaseWrapper(proAppointmentDetails);
    }

    private AppointmentState getStatusFromRequest(String status) throws PPServicesException {
        boolean isStatusPresent = hasValue(status);
        Optional<AppointmentState> statusOptional = AppointmentState.fromText(status);

        if (!isStatusPresent || !statusOptional.isPresent())
            throw new PPServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        return statusOptional.get();
    }

    @Override
    public BaseWrapper getProAppointmentDetailsList(Long serviceId, String timeZone,
                                                    String term, String status, Pageable pageable) throws ServicesException, PPServicesException {

        String userId = authorizationService.fetchLoggedInUser().getUserId();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(userId);

        if (!hasValue(proDetails))
            throw new ServicesException(GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());

        Long proId = proDetails.getId();

        LocalDate currentDate = LocalDate.now(ZoneId.of(timeZone));
        log.debug("today's date = {}", currentDate);

        LocalTime currentTime = LocalTime.now(ZoneId.of(timeZone));
        log.debug("Today time = {}", currentTime);

        LocalTime time = LocalTime.now();

        AppointmentState reqStatus = getStatusFromRequest(status);

        boolean isTermPresent = hasValue(term);
        //Get Current Appointments
        List<Integer> allowedCurrentStates = Arrays.asList(AppointmentState.CONFIRMED.value(), AppointmentState.CHECKED_IN.value(), AppointmentState.CHECKED_OUT.value());
        List<Integer> currentState;
        if (reqStatus == AppointmentState.ALL)
            currentState = allowedCurrentStates;
        else
            currentState = allowedCurrentStates.contains(reqStatus.value()) ? Arrays.asList(reqStatus.value()) : null;
        System.out.println("currentState = " + currentState + ", (hasValue(currentState) && !currentState.isEmpty()) = " + (hasValue(currentState) && !currentState.isEmpty()));

        List<AppointmentDetails> confirmedAppointmentsList = new ArrayList<>();
        Page<AppointmentDetails> confirmedAppointmentPage = null;
        if (hasValue(currentState) && !currentState.isEmpty()) {
            if (!isTermPresent)
                confirmedAppointmentPage = appointmentDetailsRepository.findByBookedForAndIdAndState(
                        proId, currentState, currentDate,serviceId, pageable);
            else
                confirmedAppointmentPage = appointmentDetailsRepository.findByBookedForAndServiceIdAndSearchTerm(
                        proId, currentState, serviceId, currentDate, CommonUtil.getLikeClauseTerm(term), pageable);
        }
        if (hasValue(confirmedAppointmentPage) && confirmedAppointmentPage.hasContent())
            confirmedAppointmentsList.addAll(confirmedAppointmentPage.getContent());

        //Get Past Appointments
        List<Integer> allowedPastState = Arrays.asList(AppointmentState.COMPLETED.value(), AppointmentState.CANCELLED.value());
        List<Integer> pastState;
        if (reqStatus == AppointmentState.ALL)
            pastState = allowedPastState;
        else
            pastState = allowedPastState.contains(reqStatus.value()) ? Arrays.asList(reqStatus.value()) : null;

        System.out.println("pastState = " + pastState + ", (hasValue(pastState) && !pastState.isEmpty()) = " + (hasValue(pastState) && !pastState.isEmpty()));
        List<AppointmentDetails> pastAppointmentsList = new ArrayList<>();
        Page<AppointmentDetails> pastAppointmentsPage = null;
        System.out.println("proId = " + proId + ", pastState = " + pastState + ", serviceId = " + serviceId);
        if (hasValue(pastState) && !pastState.isEmpty()) {
            if (!isTermPresent)
                pastAppointmentsPage = appointmentDetailsRepository.findByBookedFor_IdAndStatePast(proId, pastState, serviceId, pageable);
            else
                pastAppointmentsPage = appointmentDetailsRepository.findByBookedFor_IdAndStatePastAndSearchTerm(proId, pastState, serviceId, CommonUtil.getLikeClauseTerm(term), pageable);
        }
        if (hasValue(pastAppointmentsPage) && pastAppointmentsPage.hasContent())
            pastAppointmentsList = pastAppointmentsPage.getContent();

        List<AppointmentBookingDTO> appointmentInConfirmedState = new ArrayList<>();
        List<Long> confirmApptIds = new ArrayList<>();
        for (AppointmentDetails details : confirmedAppointmentsList) {
            if (!confirmApptIds.contains(details.getId())) {
                appointmentInConfirmedState.add(new AppointmentBookingDTO(details));
                confirmApptIds.add(details.getId());
            }
        }

        List<AppointmentBookingDTO> appointmentInPastState = new ArrayList<>();
        List<Long> pastApptIds = new ArrayList<>();
        for (AppointmentDetails details : pastAppointmentsList) {
            if (!pastApptIds.contains(details.getId())) {
                appointmentInPastState.add(new AppointmentBookingDTO(details));
                pastApptIds.add(details.getId());
            }
        }

        //set of services provided by the pro
//        Set<Services> proServices = servicesRepository.fetchServicesByProIdIn(proId, Arrays.asList(ActiveStatus.ACTIVE.value()));
        List<ProServices> proServices = proServicesRepository.fetchProActiveServiceByProId(proId, ActiveStatus.ACTIVE.value());

        List<ProvidedServiceDetailsDTO> providedServiceDetails = new ArrayList<>();

        Map<Long, String> serviceIdToNameMap = new HashMap<>();
        Map<Long, String> serviceIdToIconMap = new HashMap<>();
        if (hasValue(proServices)) {
            Page<Services> servicesPg = servicesRepository.findAllByActiveStatus(ActiveStatus.ACTIVE.value(), null);
            if (servicesPg.hasContent()) {
                serviceIdToNameMap = servicesPg.get().collect(Collectors.toMap(Services::getId, Services::getServiceName));
                serviceIdToIconMap = servicesPg.get().collect(Collectors.toMap(Services::getId, Services::getServiceIcon));
            }
        }

        int activeStatus = ActiveStatus.ACTIVE.value();
        for (ProServices proService : proServices) {
            System.out.println("proService = " + proService);
            if (proService.getActiveStatus() == activeStatus) {
                Long pserviceId = proService.getServiceId();
                providedServiceDetails.add(new ProvidedServiceDetailsDTO(pserviceId, serviceIdToNameMap.get(pserviceId),
                        serviceIdToIconMap.get(pserviceId), 0l, proService.getActiveStatus(), null,
                        proService.isCertified(), proService.isPrajekproVerified()));
            }
        }

        ProAppointmentListDTO proAppointmentList = new ProAppointmentListDTO(appointmentInConfirmedState, appointmentInPastState, providedServiceDetails);
        return new BaseWrapper(proAppointmentList);
    }

    @Override
    public BaseWrapper uploadCustomerSign(Long appointmentId, MultipartFile file) throws ServicesException {
        try {
            Optional<AppointmentDetails> detailsOptional = appointmentDetailsRepository.findById(appointmentId);

            if (!detailsOptional.isPresent()) {
                throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
            }

            FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);

            AppointmentDetails appointmentDetails = detailsOptional.get();
            appointmentDetails.updateCustomerSignDetails(fileDetailsDTO, fileUploadPath);
            appointmentDetails.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            appointmentDetailsRepository.save(appointmentDetails);

            return new BaseWrapper(appointmentId);
        } catch (IllegalStateException | IOException e) {
            log.error("Error uploading file for user ID = {}. Error = {}", appointmentId, e);
            throw new ServicesException("611");
        }
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadCustomerSign(Long appointmentId) throws ServicesException, IOException {
        Optional<AppointmentDetails> appointmentDetailsOptional = appointmentDetailsRepository.findById(appointmentId);
        if (!appointmentDetailsOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        AppointmentDetails appointmentDetails = appointmentDetailsOptional.get();

        byte[] bytes;
        String customerSignSaveDirNm = appointmentDetails.getCustomerSignSaveDirNm();
        String customerSignSavedNm = appointmentDetails.getCustomerSignSavedNm();
        if (hasValue(customerSignSaveDirNm) && hasValue(customerSignSavedNm)) {
            String filePath = appointmentDetails.getCustomerSignSaveDirNm() + appointmentDetails.getCustomerSignSavedNm();
            bytes = Files.readAllBytes(Paths.get(filePath));
        } else {
            bytes = new byte[]{};
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + appointmentDetails.getCustomerSignDisplayNm());
        responseHeaders.add("file-extension", appointmentDetails.getCustomerSignExtn());

        return new ResponseEntity(bytes, responseHeaders, HttpStatus.OK);
    }

    @Override
    public BaseWrapper generateInvoice(Long appointmentId, GenerateInvoiceWrapperDTO request) throws
            ServicesException {

        if (appointmentId == null || appointmentId == 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }
        Optional<AppointmentDetails> appointmentDetailsOptional = appointmentDetailsRepository.findById(appointmentId);
        if (!appointmentDetailsOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        final String loggedInUserEmail = authorizationService.fetchLoggedInUser().getEmailId();
        final int activeStatus = ActiveStatus.ACTIVE.value();

        AppointmentDetails appointmentDetails = appointmentDetailsOptional.get();
        ProDetails proDetails = appointmentDetails.getBookedFor();
        Double totalCost = 0.0d;
        // generate Invoice number for appointment
        AppointmentInvoice appointmentInvoice = appointmentInvoiceRepository.findByAppointmentId(appointmentId);
        if (hasValue(appointmentInvoice)) {
            log.debug("invoice is updated");
            appointmentInvoice.updateAuditableFields(false, loggedInUserEmail, activeStatus);
        } else {
            log.debug("new invoice generated ");
            appointmentInvoice = new AppointmentInvoice();
            Integer invoiceNo = generateInvoiceNo();
            appointmentInvoice.setInvoiceNo(invoiceNo);
            appointmentInvoice.setInvoiceTs(System.currentTimeMillis());
            appointmentInvoice.updateAuditableFields(true, loggedInUserEmail, activeStatus);
        }

        appointmentInvoice.setAppointmentDetails(appointmentDetails);
        appointmentInvoiceRepository.save(appointmentInvoice);

//        List<String> configName = Arrays.asList(ConfigEnum.APPOINTMENT_PRO_CNCL_PNLTY_PERCENTAGE.name(), ConfigEnum.APPOINTMENT_COMPLETION_PRAJEKPRO_PERCENT.name()
//                , ConfigEnum.WALLET_DEFAULT_LIMIT.name());
//        List<Configuration> configuration = configurationRepository.findByConfigNameIn(configName);
//        Float proPercent = 0.0f;
//        Float prajekProPercent = 0.0f;
//        Float walletDefaultLimit = 0.0f;
//        for (Configuration config : configuration) {
//            if (config.getConfigName().equals(ConfigEnum.APPOINTMENT_PRO_CNCL_PNLTY_PERCENTAGE.name())) {
//                proPercent = Float.valueOf(config.getConfigValue());
//            }
//            if (config.getConfigName().equals(ConfigEnum.APPOINTMENT_COMPLETION_PRAJEKPRO_PERCENT.name())) {
//                prajekProPercent = Float.valueOf(config.getConfigValue());
//            }
//            if (config.getConfigName().equals(ConfigEnum.WALLET_DEFAULT_LIMIT.name())) {
//                walletDefaultLimit = Float.valueOf(config.getConfigValue());
//            }
//        }

        //Appointments Other Services
        final int inactiveStatus = ActiveStatus.INACTIVE.value();
        //Deactivate all other services in the appt. and collect the index position of each record
        Map<Long, Integer> otherServiceIdIndexMap = new HashMap<>();
        for (int index = 0; index < appointmentDetails.getAppointmentOtherServices().size(); index++) {
            appointmentDetails.getAppointmentOtherServices().get(index).setActiveStatus(inactiveStatus);
            otherServiceIdIndexMap.put(appointmentDetails.getAppointmentOtherServices().get(index).getId(), index);
        }

        //Add/Update only those other services which are sent in request
        if (hasValue(request.getAppointmentServicesDTO())) {
            List<AppointmentOtherServicesDTO> requestedOtherServices = request.getAppointmentOtherServices();
            for (AppointmentOtherServicesDTO otherServices : requestedOtherServices) {
                Long otherServiceId = otherServices.getId();

                AppointmentOtherServices appointmentOtherServices;
                boolean isExitingOthrService = otherServiceIdIndexMap.containsKey(otherServiceId);
                Integer existingServiceIndex = otherServiceIdIndexMap.get(otherServiceId);
                if (hasValue(otherServiceId) && otherServiceId > 0 && isExitingOthrService) {
                    appointmentOtherServices = appointmentDetails.getAppointmentOtherServices().get(existingServiceIndex);
                } else {
                    appointmentOtherServices = new AppointmentOtherServices();
                    appointmentOtherServices.setId(otherServices.getId());
                }

                appointmentOtherServices.setServiceName(otherServices.getServiceName());

                appointmentOtherServices.setReqQuantity(otherServices.getReqQuantity());
                appointmentOtherServices.setUnitPrice(otherServices.getUnitPrice());
                totalCost = totalCost + ((otherServices.getUnitPrice()) * (otherServices.getReqQuantity()));

                appointmentOtherServices.setAppointmentDetails(appointmentDetails);
                appointmentOtherServices.updateAuditableFields(true, loggedInUserEmail, activeStatus);

                if (isExitingOthrService)
                    appointmentDetails.getAppointmentOtherServices().set(existingServiceIndex, appointmentOtherServices);
                else
                    appointmentDetails.getAppointmentOtherServices().add(appointmentOtherServices);
            }
        }


        //Add/Update the existing appointment requested services, categories and sub-categories
        int size = appointmentDetails.getAppointmentRequestedServices().get(0)
                .getAppointmentRequestedServiceCategories().get(0)
                .getAppointmentRequestedServiceSubCategories().size();
        Map<Long, Integer> existingServiceSubCategoryIdIndexMap = new HashMap<>();
        //Deactivate all existing appt. requested subcategories
        for (int index = 0; index < size; index++) {
            existingServiceSubCategoryIdIndexMap.put(
                    appointmentDetails
                            .getAppointmentRequestedServices().get(0)
                            .getAppointmentRequestedServiceCategories().get(0)
                            .getAppointmentRequestedServiceSubCategories()
                            .get(index)
                            .updateStatusAndGetId(inactiveStatus),
                    index);
        }

        AppointmentServicesDTO reqAppointmentServiceDetails = request.getAppointmentServicesDTO();
        AppointmentRequestedServices service = appointmentDetails.getAppointmentRequestedServices().get(0);

        int catInx = 0;
        for (AppointmentServiceCategoryDTO reqServiceCategory : reqAppointmentServiceDetails.getAppointmentServiceCategory()) {
            //TODO: Remove below workaround and check on UI side why this value (appointmentRequestedServiceCategoryId) is not being sent correctly
            Long appointmentRequestedServiceCategoryId = reqServiceCategory.getAppointmentRequestedServiceCategoryId();
            Long existingApptReqSrvCatId = service.getAppointmentRequestedServiceCategories().get(catInx).getId();
            if (hasValue(appointmentRequestedServiceCategoryId) && appointmentRequestedServiceCategoryId != existingApptReqSrvCatId)
                reqServiceCategory.setAppointmentRequestedServiceCategoryId(existingApptReqSrvCatId);

            AppointmentRequestedServiceCategories category = new AppointmentRequestedServiceCategories(reqServiceCategory, service);

            for (AppointmentServiceSubCategoryDTO reqServiceSubCategory : reqServiceCategory.getAppointmentServiceSubCategory()) {

                Long reqServiceSubCatId = reqServiceSubCategory.getAppointmentRequestedServiceSubCategoryId();
                log.info("reqServiceSubCategory id = {}", reqServiceSubCatId);

                AppointmentRequestedServiceSubCategories reqSubCategory;
                boolean isExistingSubCategory = existingServiceSubCategoryIdIndexMap.containsKey(reqServiceSubCatId);
                Integer index = existingServiceSubCategoryIdIndexMap.get(reqServiceSubCatId);
                System.out.println("reqServiceSubCatId = " + reqServiceSubCatId + ", isExistingSubCategory = " + isExistingSubCategory);
                if (hasValue(reqServiceSubCatId) && reqServiceSubCatId > 0 && isExistingSubCategory) {
                    reqSubCategory = appointmentDetails
                            .getAppointmentRequestedServices().get(0)
                            .getAppointmentRequestedServiceCategories().get(0)
                            .getAppointmentRequestedServiceSubCategories()
                            .get(index);
                    reqSubCategory.updateAuditableFields(false, loggedInUserEmail, activeStatus);
                } else {
                    reqSubCategory = new AppointmentRequestedServiceSubCategories();
                    reqSubCategory.setId(reqServiceSubCategory.getAppointmentRequestedServiceSubCategoryId());
                    reqSubCategory.setAppointmentRequestedServiceCategories(category);
                    reqSubCategory.setServiceItemSubCategory(new ServiceItemSubCategory(reqServiceSubCategory.getServiceSubCategoryId()));
                    reqSubCategory.updateAuditableFields(true, loggedInUserEmail, activeStatus);
                }

                //Update Fields From Request
                reqSubCategory.setRequestedPrice(reqServiceSubCategory.getReqPrice());
                reqSubCategory.setRequestedQty(reqServiceSubCategory.getReqQty());

                totalCost = totalCost + ((reqSubCategory.getRequestedPrice()) * (reqSubCategory.getRequestedQty()));

                if (isExistingSubCategory) {
                    appointmentDetails
                            .getAppointmentRequestedServices().get(0)
                            .getAppointmentRequestedServiceCategories().get(0)
                            .getAppointmentRequestedServiceSubCategories()
                            .set(index, reqSubCategory);
                } else
                    appointmentDetails
                            .getAppointmentRequestedServices().get(0)
                            .getAppointmentRequestedServiceCategories().get(0)
                            .getAppointmentRequestedServiceSubCategories()
                            .add(reqSubCategory);
            }
        }

        appointmentDetails.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        appointmentDetails.setAppointmentInvoice(appointmentInvoice);

        appointmentDetails.setSubTotal(totalCost);

        //Set Cancellation Pnlty Amt.
//        Double proLockedAmount = totalCost * (proPercent / 100f);
//        appointmentDetails.setCancellationPnltyLockedAmount(proLockedAmount);

        //Set PrajekPro Commission Amt.
//        Double prePrajekProLockedAmount = appointmentDetails.getPrajekProLockedAmount();
//        Double prajekProLockedAmount = totalCost * (prajekProPercent / 100f);
//        appointmentDetails.setPrajekProLockedAmount(prajekProLockedAmount);

        appointmentDetails.setGrandTotal(request.getGrandTotal());
        List<AppointmentTaxDetails> appointmentTaxDetailsList = getAppointmentTaxDetailsList(request.getApplicableTaxes(), appointmentDetails);
        appointmentDetails.setAppointmentTaxDetailsList(appointmentTaxDetailsList);

        appointmentDetailsRepository.save(appointmentDetails);

        //Calculate PRO Wallet locked amt.
        ProWalletDetails proWalletDetails = walletDetailsRepository.findByProDetails_IdAndActiveStatus(proDetails.getId(), ActiveStatus.ACTIVE.value());
        Double walletAmount = proWalletDetails.getAmount();
        Double preProLockedAmt = appointmentDetails.getPrajekProLockedAmount();
        //walletAmount = walletAmount + preProLockedAmt;

//        Double lockedAmount = totalCost * ((prajekProPercent + proPercent) / 100f);
//        walletAmount = walletAmount - lockedAmount;

        //Check if final wallet amount after all deductions including the locked Amt. is = or > min. allowed threshold value
        if (walletAmount < preProLockedAmt)
            throw new ServicesException(GeneralErrorCodes.ERR_INSUFFICIENT_BALANCE_IN_WALLET.value());

        proWalletDetails.setAmount(walletAmount);
        proWalletDetails.setLockedAmount(0.0);
        walletDetailsRepository.save(proWalletDetails);

        return new BaseWrapper(appointmentDetails.getAppointmentInvoice().getId());
    }


    /* @Override
     public BaseWrapper getParticularToAddServices() throws ServicesException {
         String userId = authorizationService.fetchLoggedInUser().getUserId();
         ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(userId);
         if (!CheckUtil.hasValue(proDetails)) {
             throw new ServicesException(GeneralErrorCodes.ERR_INVALID_USERNAME.value());
         }
         Long proId = proDetails.getId();
         Set<Services> proServices = proDetailsRepository.fetchServicesByProIdIn(proDetails.getId());
         List<ProServiceListDTO> particulars = new ArrayList<>();
         for (Services service : proServices) {
             Long serviceId = service.getId();
             List<ProServiceItemsPricing> proServiceItemsPricings = proServiceItemsPricingRepository.findByProDetails_IdAndServices_Id(proId, serviceId);

             Optional<Services> servicesOptional = servicesRepository.findById(serviceId);
             if (!servicesOptional.isPresent()) {
                 throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
             }

             Services services = servicesOptional.get();
             List<ServiceItemCategoriesDTO> serviceCategories = new ArrayList<>();
             List<ServiceItemCategory> serviceItemCategories = serviceItemCategoryRepository.findByServices_IdAndActiveStatus(serviceId, ActiveStatus.ACTIVE.value());
            if(!serviceItemCategories.isEmpty()){
                for (ServiceItemCategory serviceItemCategory : serviceItemCategories) {

                    ServiceItemCategoriesDTO category = new ServiceItemCategoriesDTO(serviceItemCategory);

                    List<ServiceItemSubCategoryDTO> serviceSubCategories = new ArrayList<>();

                    List<ServiceItemSubCategory> serviceItemSubCategories = serviceItemSubCategoryRepository.findByServiceItemCategory_IdAndActiveStatus(serviceItemCategory.getId(), ActiveStatus.ACTIVE.value());

                    for (ServiceItemSubCategory serviceItemSubCategory : serviceItemSubCategories) {
                        ServiceItemSubCategoryDTO subcategory = new ServiceItemSubCategoryDTO(serviceItemSubCategory);
                        for (ProServiceItemsPricing subcategoryPrice : proServiceItemsPricings) {
                            if (serviceId == subcategoryPrice.getServices().getId() &&
                                    category.getId() == subcategoryPrice.getServiceItemSubcategory().getServiceItemCategory().getId() &&
                                    subcategory.getId() == subcategoryPrice.getServiceItemSubcategory().getId()) {
                                subcategory.setProSubCategoryPrice(subcategoryPrice.getPrice());
                                subcategory.setProServicePricingId(subcategoryPrice.getId());
                                serviceSubCategories.add(subcategory);
                            }
                        }

                    }
                    category.setServiceItemSubCategoryList(serviceSubCategories);
                    serviceCategories.add(category);
                }

                ProServiceListDTO proServiceList = new ProServiceListDTO(services, serviceCategories);
                particulars.add(proServiceList);
            }
         }
         return new BaseWrapper(particulars);
     }
 */
    @Override
    public BaseWrapper storeUserAppointmentAddress(UserDeliveryAddressDTO userAddress) {
        String userId = authorizationService.fetchLoggedInUser().getUserId();

        UserDeliveryAddress userDeliveryAddress = new UserDeliveryAddress(userAddress, userId);
        userDeliveryAddress.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        userDeliveryAddressRepository.save(userDeliveryAddress);

        return new BaseWrapper(userDeliveryAddress.getId());
    }

    @Override
    public BaseWrapper getUserAppointmentAddress() throws ServicesException {

        String userId = authorizationService.fetchLoggedInUser().getUserId();

        List<UserDeliveryAddress> userDeliveryAddress = userDeliveryAddressRepository.findByUsers_UserIdAndActiveStatus(userId, ActiveStatus.ACTIVE.value());
        log.debug("list of address is empty = {}", userDeliveryAddress.isEmpty());

        List<UserDeliveryAddressDTO> userDeliveryAddressDTO = new ArrayList<>();

        for (UserDeliveryAddress deliveryAddress : userDeliveryAddress) {

            if (hasValue(deliveryAddress)) {

                userDeliveryAddressDTO.add(new UserDeliveryAddressDTO(deliveryAddress));
                log.debug("user address added");
            }

        }

        return new BaseWrapper(userDeliveryAddressDTO);
    }

    @Override
    public BaseWrapper deleteUserAppointmentAddress(Long addressId) throws ServicesException {
        if (addressId == null || addressId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        Optional<UserDeliveryAddress> userDeliveryAddressOptional = userDeliveryAddressRepository.findById(addressId);

        if (!userDeliveryAddressOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        UserDeliveryAddress userDeliveryAddress = userDeliveryAddressOptional.get();

        userDeliveryAddress.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.INACTIVE.value());
        userDeliveryAddressRepository.save(userDeliveryAddress);

        return new BaseWrapper(userDeliveryAddress.getId());
    }

    @Override
    public BaseWrapper getProjectOverview() {

        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());
        Long proId = proDetails.getId();

//        Set<Services> proServices = proDetailsRepository.fetchServicesByProIdIn(proId);
        Set<Services> proServices = servicesRepository.fetchServicesByProIdIn(proId, Arrays.asList(ActiveStatus.ACTIVE.value()));

        List<ProvidedServiceDetailsDTO> providedServiceDetails = new ArrayList<>();

        Long appointmentCount = 0l;
        Long totalAppointments = 0l;
        List<Integer> state = Arrays.asList(AppointmentState.COMPLETED.value());
        for (Services service : proServices) {
            appointmentCount = appointmentDetailsRepository.countOfProAppByServiceAndState(proId, state, service.getId());
            providedServiceDetails.add(new ProvidedServiceDetailsDTO(service.getId(), service.getServiceName(), appointmentCount));
            totalAppointments = totalAppointments + appointmentCount;
        }

        List<ProReviews> proReviews = proDetails.getReviews();
        List<ProReviewsDTO> proReviewsDTOS = new ArrayList<>();
        Double averageRating = 0d;
        for (ProReviews reviews : proReviews) {
            averageRating = averageRating + reviews.getStarRating();
            proReviewsDTOS.add(new ProReviewsDTO(reviews));
        }
        averageRating = averageRating / new Integer(proReviews.size()).floatValue();
        log.debug("average rating = {}", averageRating);
        if (averageRating.isNaN()) {
            averageRating = 0d;
        }

        ProjectOverviewDTO projectOverviewDTO = new ProjectOverviewDTO(totalAppointments, providedServiceDetails, averageRating, proReviews.size());


        return new BaseWrapper(projectOverviewDTO);
    }

    @Override
    public BaseWrapper getProReview(Pageable pageable) {
        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());
        Long proId = proDetails.getId();

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
    public BaseWrapper appointmentSearch(AppointmentSearchRequestBodyDTO request, Pageable pageable) throws
            ServicesException {

        //if there is no request body
        List<AppointmentDetails> appointmentDetailsList;
        Page<AppointmentDetails> appointmentDetailsPage = getSearchAppointmentList(request, pageable);

        appointmentDetailsList = appointmentDetailsPage.getContent();

        List<AppointmentBookingDTO> appointmentList = new ArrayList<>();

        if (appointmentDetailsList.isEmpty()) {
            return new BaseWrapper(appointmentList);
        }

        for (AppointmentDetails details : appointmentDetailsList) {
            appointmentList.add(new AppointmentBookingDTO(details));
        }

        Pagination pagination = new Pagination(appointmentList, appointmentDetailsPage.getTotalElements(), pageable);
        return new BaseWrapper(appointmentList, pagination);
    }

    @Override
    public BaseWrapper getAppointmentList(AppointmentSearchRequestBodyDTO request, Pageable pageable) {
        //if there is no request body
        List<AppointmentDetails> appointmentDetailsList;
        Sort sort = Sort.by("appointmentRequestedServices.appointmentDate").descending().and(Sort.by("appointmentRequestedServices.appointmentTime").descending());
        if (pageable.isPaged()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        } else {
            pageable = PageRequest.of(0, 10, sort);
        }

        Page<AppointmentDetails> appointmentDetailsPage = getSearchAppointmentList(request, pageable);

        appointmentDetailsList = appointmentDetailsPage.getContent();

        List<AppointmentDTO> appointmentList = new ArrayList<>();

        if (appointmentDetailsList.isEmpty()) {
            return new BaseWrapper(appointmentList);
        }

        for (AppointmentDetails details : appointmentDetailsList) {
            appointmentList.add(new AppointmentDTO(details));
        }

        Pagination pagination = new Pagination(appointmentList, appointmentDetailsPage.getTotalElements(), pageable);
        return new BaseWrapper(appointmentList, pagination);
    }

    @Override
    public BaseWrapper sendInvoice(Long appointmentId) throws ServicesException, ParseException {
        if (!hasValue(appointmentId) || appointmentId == 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        Optional<AppointmentDetails> appointmentDetailsOptional = appointmentDetailsRepository.findById(appointmentId);
        if (!appointmentDetailsOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        AppointmentDetails appointmentDetails = appointmentDetailsOptional.get();
        Users pro = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(pro.getUserId());
        Users notificationUser = usersRepository.findById(appointmentDetails.getBookedBy().getUserId()).get();

        List<AppointmentRequestedServices> appointmentRequestedServices = appointmentDetails.getAppointmentRequestedServices();

        String appointmentDateTime = "";
        for (AppointmentRequestedServices requestedServices : appointmentRequestedServices) {
            appointmentDateTime = String.format("%s %s", requestedServices.getAppointmentDate(), requestedServices.getAppointmentTime());
        }
        // send notification
        String appointmentDateTimeForNotification = (new DateUtil()).convertDateFormat(appointmentDateTime);
        String sendInvoiceMessageNotification = String.format(completedAppointmentStatusMessage, appointmentDateTimeForNotification, pro.getFullName());
        sendNotification(notificationUser, AppointmentNotificationEnum.INVOICE_GENERATED.value(), sendInvoiceMessageNotification, pro);

        appointmentDetails.setState(AppointmentState.COMPLETED);
        appointmentDetailsRepository.save(appointmentDetails);

        //send email after appointment completed
        String custEmail = appointmentDetails.getBookedBy().getEmailId();
        String emailBody = MessageFormat.format(
                env.getProperty("email.appointment.body"),
                appointmentDetails.getBookedBy().getFullName(),
                appointmentDateTimeForNotification,
                appointmentDetails.getBookedFor().getUserDetails().getFullName());
        emailService.sendEmail(custEmail,
                env.getProperty("email.appointment.title"), emailBody);

        return new BaseWrapper();
    }

    @Override
    public BaseWrapper initiateAppointmentPayment(Long appointmentId) throws
            ServicesException, IOException, ApiException {

        AppointmentDetails appointmentDetails = validateAppointmentId(appointmentId);

        ProDetails proDetails = appointmentDetails.getBookedFor();

        Double grandTotal = appointmentDetails.getGrandTotal();

        Currency currency = getCurrency(appointmentDetails);

        int transactionId = walletHelper.generateTransactionId();

        //Set payment table
        TransactionType transactionType = TransactionType.APPOINTMENT;
        PaymentDetails paymentDetails = new PaymentDetails(proDetails, GlobalConstants.PaymentMethods.PAYPAL, PaymentStatus.INITIATED.name(),
                transactionId, transactionType, PaymentsResponse.ResultCodeEnum.AUTHORISED.name(), true, GlobalConstants.USER_API,
                ActiveStatus.PAYMENT_INITIATED.value(), null);
        paymentDetailsRepository.save(paymentDetails);

        //Update Wallet Transaction History
        walletHelper.addProWalletTransactionHistory(proDetails, paymentDetails, transactionId, grandTotal,
                transactionType, appointmentDetails, GlobalConstants.USER_API, ActiveStatus.PAYMENT_INITIATED.value());

        //Prepare Initiate Payment Response and send
        return new BaseWrapper(paymentHelper.initiatePaymentResponse(transactionId, grandTotal));
    }


    @Override
    public void updateAppointmentTables(Integer txdId, PaymentDetails paymentDetails, PPPaymentResponseType
            paymentResponse) {
        boolean isSuccessfulPayment = CommonUtil.isSuccessfulPayment(paymentResponse);

        int activeStatus = isSuccessfulPayment ? ActiveStatus.ACTIVE.value() : ActiveStatus.PAYMENT_FAILED.value();

        //update Wallet Transaction History table
        WalletTransactionHistory walletTransactionHistory = walletTransactionHistoryRepository.getTransactionHistoryByTXDId(txdId);
        walletTransactionHistory.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
        walletTransactionHistoryRepository.save(walletTransactionHistory);

        if (isSuccessfulPayment) {
            AppointmentDetails appointmentDetails = walletTransactionHistory.getAppointmentDetails();

            //Calculate Prajekpro commission amount and cancellation penalty charges
            double prajekProCommision = null == appointmentDetails.getPrajekProLockedAmount() ? 0 : appointmentDetails.getPrajekProLockedAmount();
            double cancellationPenaltyCharges = null == appointmentDetails.getCancellationPnltyLockedAmount() ? 0 : appointmentDetails.getCancellationPnltyLockedAmount();

            //Calculate total locked amount to be released
            ProWalletDetails proWalletDetails = walletTransactionHistory.getProWalletDetails();

            double lockedAmount = prajekProCommision + cancellationPenaltyCharges;
            if (hasValue(proWalletDetails.getLockedAmount()) && proWalletDetails.getLockedAmount() > 0.0f) {
                lockedAmount = proWalletDetails.getLockedAmount() - lockedAmount;
            } else
                lockedAmount = 0.0d;
            //Release locked amount
            proWalletDetails.setLockedAmount(lockedAmount);

            //Calculate the final wallet amount to be, after releasing the cancellation penalty charges
            double walletAmount = (null == proWalletDetails.getAmount() ? 0 : proWalletDetails.getAmount()) + cancellationPenaltyCharges;
            //Release the cancellation penalty charges
            proWalletDetails.setAmount(walletAmount);

            //Update PRO Wallet Details (final wallet and locked amounts) on successfull transaction
            proWalletDetails.updateAuditableFields(true, GlobalConstants.USER_API, activeStatus);
            walletDetailsRepository.save(proWalletDetails);

            //Add Wallet Top Up History entry
            ProDetails proDetails = proWalletDetails.getProDetails();
            TransactionType transactionType = walletTransactionHistory.getTransactionType();

            WalletTopUpHistory walletTopUpHistory = new WalletTopUpHistory(5L, cancellationPenaltyCharges, proDetails, proWalletDetails);
            walletTopUpHistory.setTransactionType(transactionType);
            walletTopUpHistory.setWalletTransactionHistory(walletTransactionHistory);
            walletTopUpHistory.updateAuditableFields(true, GlobalConstants.USER_API, activeStatus);
            walletTopUpHistoryRepository.save(walletTopUpHistory);

            //Update Prajekpro Wallet Details on successfull/failure transaction
            walletHelper.addPrajekProWalletTransaction(prajekProCommision, WalletAmountType.APPOINTMENT_COMISSION, activeStatus,
                    proDetails, appointmentDetails, walletTransactionHistory);
        }
    }

    @Override
    @ApiOperation(value = "Api to cancel Appt. from Customer App")
    public BaseWrapper initiateAppointmentCancellation(Long appointmentId) throws ServicesException, IOException, ApiException, ParseException {
        //Validate and get Appt. details
        AppointmentDetails appointmentDetails = validateAppointmentId(appointmentId);

        //Fetch Logged In User Details
        Users loggedInUser = authorizationService.fetchLoggedInUser();

        //Get requested services for the appointment
        List<AppointmentRequestedServices> requestedServices = appointmentDetails.getAppointmentRequestedServices();

        System.out.println("Appt. state = " + appointmentDetails.getState() + ", isBookedState = " + (appointmentDetails.getState() == AppointmentState.BOOKED));
        if (appointmentDetails.getState() == AppointmentState.BOOKED) {
            //Update Appointment status
            updateAppointmentState(appointmentId, AppointmentState.CANCELLED, null, requestedServices.get(0).getId(), loggedInUser);

            //Prepare Initiate Payment Response and send
            return new BaseWrapper(new InitiatePaymentResponse(false));
        }

        ProDetails proDetails = appointmentDetails.getBookedFor();
        long proId = proDetails.getId();
        Double totalCost = appointmentDetails.getSubTotal();
        Double lockedAmount = 0.0d;

        if (hasValue(appointmentDetails.getPrajekProLockedAmount()) || hasValue(appointmentDetails.getCancellationPnltyLockedAmount())) {
            lockedAmount = appointmentDetails.getCancellationPnltyLockedAmount() + appointmentDetails.getPrajekProLockedAmount();
        }

        //Get cancellation config details
        Long cancellationFees = 0l;
        String cancellationFeesUnit = null;
        Currency currency = null;
        Object[] proCancellationConfigDetails = new Object[0];
        String apptDate = null;
        String apptTime = null;
        for (AppointmentRequestedServices service : requestedServices) {
            //Update Appt. Date & Time details
            apptDate = service.getAppointmentDate();
            apptTime = service.getAppointmentTime();

            //Update Cancellation Fees Details
            cancellationFees = service.getServices().getCancellationFees();
            cancellationFeesUnit = service.getServices().getCancellationFeesUnit().getValue();

            //Get the configuration for cancellation policies
            proCancellationConfigDetails = getProCancellationConfigDetails(proId, service);

            for (AppointmentRequestedServiceCategories category : service.getAppointmentRequestedServiceCategories()) {
                for (AppointmentRequestedServiceSubCategories subcategory : category.getAppointmentRequestedServiceSubCategories()) {
                    currency = subcategory.getServiceItemSubCategory().getCurrency();
                    break;
                }

                if (null != currency)
                    break;
            }

            if (null != cancellationFeesUnit && null != currency && cancellationFees > 0l
                    && null != proCancellationConfigDetails && proCancellationConfigDetails.length == 3
                    && null != apptDate && null != apptTime)
                break;
        }

        //Get days diff between Appt. date and current date
        String apptDateTimeString = MessageFormat.format("{0}T{1}Z", apptDate, apptTime);
        long apptDateTimeInMillis = ZonedDateTime.parse(apptDateTimeString).toInstant().toEpochMilli();
        Long diff = 0l;

        Long proConfigCancellationUnitId = (long) proCancellationConfigDetails[2];
        if (proConfigCancellationUnitId.equals(CancellationUnit.DAYS.value()))
            diff = DateUtil.getDiffInDays(System.currentTimeMillis(), apptDateTimeInMillis);
        else if (proConfigCancellationUnitId.equals(CancellationUnit.HOURS.value()))
            diff = DateUtil.getDiffInHours(System.currentTimeMillis(), apptDateTimeInMillis);
        System.out.println("apptDateTimeString = " + apptDateTimeString + ", diff = " + diff
                + ", cancellation time = " + proCancellationConfigDetails[0] + ", Unit Value = " + proCancellationConfigDetails[1] +
                ", Unit ID = " + proCancellationConfigDetails[2]
                + ", proConfigCancellationUnitId.equals(CancellationUnit.DAYS.value()) = " + proConfigCancellationUnitId.equals(CancellationUnit.DAYS.value())
                + ", proConfigCancellationUnitId.equals(CancellationUnit.HOURS.value()) = " + proConfigCancellationUnitId.equals(CancellationUnit.HOURS.value()));

        //If cancellation is been done before allowed time of free cancellation then update Appointment status and return response
        if (diff >= ((long) proCancellationConfigDetails[0])) {
            //Update Appointment status
            updateAppointmentState(appointmentId, AppointmentState.CANCELLED, null, requestedServices.get(0).getId(), loggedInUser);

            //Prepare Initiate Payment Response and send
            return new BaseWrapper(new InitiatePaymentResponse(false));
        }

        //Else create a transaction for cancellation fees

        //Get Final Cancellation Amount
        Double finalCancellationAmount = cancellationFeesUnit.equals("Absolute") ? cancellationFees.doubleValue() : (totalCost * (cancellationFees.floatValue() / 100f));

        int transactionId = walletHelper.generateTransactionId();
        //Set payment table
        TransactionType transactionType = TransactionType.CUST_APPOINTMENT_CANCEL;
        PaymentDetails paymentDetails = new PaymentDetails(proDetails, GlobalConstants.PaymentMethods.PAYPAL, PaymentStatus.INITIATED.name(),
                transactionId, transactionType, PaymentsResponse.ResultCodeEnum.AUTHORISED.name(), true, GlobalConstants.USER_API,
                ActiveStatus.PAYMENT_INITIATED.value(), null);
        paymentDetailsRepository.save(paymentDetails);

        // set pro wallet table
        //Update Wallet Transaction History
        System.out.println("finalCancellationAmount = " + finalCancellationAmount);
        walletHelper.addProWalletTransactionHistoryAndTopUpHistory(proDetails, paymentDetails, transactionId, finalCancellationAmount, finalCancellationAmount, transactionType, appointmentDetails);

        //Prepare Initiate Payment Response and send
        return new BaseWrapper(paymentHelper.initiatePaymentResponse(transactionId, finalCancellationAmount));
    }

    private AppointmentDetails validateAppointmentId(Long appointmentId) throws ServicesException {
        Optional<AppointmentDetails> appointmentDetailsOptional = appointmentDetailsRepository.findById(appointmentId);
        if (!appointmentDetailsOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

        return appointmentDetailsOptional.get();
    }


    @Override
    public void updateAppointmentCancellationTables(Integer txdId, PaymentDetails paymentDetails, PPPaymentResponseType ppPaymentResponseFromResultCode) throws ServicesException, ParseException {

        boolean isSuccessfulPayment = CommonUtil.isSuccessfulPayment(ppPaymentResponseFromResultCode);

        int activeStatus = isSuccessfulPayment ? ActiveStatus.ACTIVE.value() : ActiveStatus.PAYMENT_FAILED.value();

        //update Wallet Transaction History table
        WalletTransactionHistory walletTransactionHistory = walletTransactionHistoryRepository.getTransactionHistoryByTXDId(txdId);
        walletTransactionHistory.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
        walletTransactionHistoryRepository.save(walletTransactionHistory);

        //update wallet topup History table
        WalletTopUpHistory topUpHistory = walletTopUpHistoryRepository.getTopupHistoryByTxdId(txdId);
        topUpHistory.updateAuditableFields(false, GlobalConstants.USER_API, activeStatus);
        walletTopUpHistoryRepository.save(topUpHistory);

        if (isSuccessfulPayment) {
            AppointmentDetails appointmentDetails = walletTransactionHistory.getAppointmentDetails();

            //Update Appt. state to `CANCEL`
            appointmentDetails.setState(AppointmentState.CANCELLED);
            appointmentDetailsRepository.save(appointmentDetails);

            //Update PRO Wallet details
            if (paymentDetails.getTransactionType() == TransactionType.CUST_APPOINTMENT_CANCEL) {
                //Release All Locked Amount for the appointment and reimburse the cancellation penalty collected from customer
                walletHelper.releaseAllApptLockedAmtToProWalletWithCancellationReimbursement(appointmentDetails, topUpHistory.getAmount());
            }
            //TODO: Check If PRO will be paying anything or we will be deducting directly from his already locked amount
//            else if (paymentDetails.getTransactionType() == TransactionType.APPOINTMENT_CANCEL) {
//                //Release appointment commission amount to PRO wallet
//                walletHelper.releaseApptCommissionAmtToProWallet(appointmentDetails);
//
//                //Add Cancellation Penalty paid by PRO to PrajekPro Wallet
//                Double apptCancellationPnltyAmt = appointmentDetails.getCancellationPnltyLockedAmount();
//                walletHelper.addPrajekProWalletTransaction(apptCancellationPnltyAmt, WalletAmountType.CANCELLATION_PENALTY, activeStatus,
//                        appointmentDetails.getBookedFor(), appointmentDetails, walletTransactionHistory);
//
//                //Deduct cancellation penalty from PRO wallet locked amount
//                walletHelper.deductAppCancellationPenaltyAmtFromProLockedAmt(appointmentDetails);
//            }
        }
    }

    @Override
    public InvoiceDtl selectAppointmentInvoiceDtl(Long apptId) throws ServicesException {
        AppointmentBookingDTO appointmentBookingDtls = (AppointmentBookingDTO) getAppointmentDetails(apptId).getResponse();
        if (!hasValue(appointmentBookingDtls))
            throw new ServicesException("610");

        return appointmentBookingDtls.getInvoiceDtl();
    }

    @Autowired
    private ApptDocsRepository apptDocsRepository;

    @Override
    public BaseWrapper updateProApptDocs(Long apptId, DocType docType, MultipartFile file) throws ServicesException, IOException {
        Optional<AppointmentDetails> appointmentDetailsOpt = appointmentDetailsRepository.findById(apptId);
        //Check if appointment exists
        if (!appointmentDetailsOpt.isPresent())
            throw new ServicesException(610);

        //Save the uploaded file
        FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);

        //Save the details of uploaded file
        ApptDocs apptDocs = new ApptDocs(docType, fileDetailsDTO, appointmentDetailsOpt.get());
        apptDocs.updateAuditableFields(true, commonUtility.getLoggedInUserId(), ActiveStatus.ACTIVE.value());
        apptDocsRepository.save(apptDocs);

        return new BaseWrapper(apptDocs.getId());
    }

    @Override
    public DownloadImageDTO getApptDocs(String id) throws ServicesException {
        Optional<ApptDocs> apptDocumentsOpt = apptDocsRepository.findById(Long.parseLong(id));
        if (!apptDocumentsOpt.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        ApptDocs apptDocument = apptDocumentsOpt.get();

        return apptDocument.convertToDowloadImageDTO();
    }

    @Override
    public List<AppointmentDetailsRepository.ProJobsCompleted> getJobsCompletedForProIdIn(List<Long> prosWithStartingCostIds) throws ServicesException {
        CommonUtility.validateInput(prosWithStartingCostIds);

        return appointmentDetailsRepository.countJobsCompletedForProIdIn(prosWithStartingCostIds,
                Arrays.asList(AppointmentState.COMPLETED.value()), Arrays.asList(ActiveStatus.ACTIVE.value()));
    }

    private void getDuplicateRecords(AppointmentServicesDTO appointmentServicesDTO, Long bookedFor) throws
            ServicesException {
        String appointmentDate = appointmentServicesDTO.getDate();
        // LocalDate appointmentDate = LocalDate.parse(appointmentDateString);
        Long appointmentTimeId = appointmentServicesDTO.getTimeSlot().getId();
        Integer appointmentCount = appointmentRequestedServicesRepository.countProBookedAppointmentsByDateAndTimeAndServiceId(bookedFor, appointmentDate, appointmentTimeId, AppointmentState.CANCELLED);

        if (appointmentCount != 0)
            throw new ServicesException(GeneralErrorCodes.ERR_DUPLICATE_RECORD_EXISTS.value());
    }

    private Page<AppointmentDetails> getSearchAppointmentList(AppointmentSearchRequestBodyDTO request, Pageable
            pageable) {

        //Prepare Appointment Predicate
        Specification<AppointmentDetails> spec = new SearchAppointmentSpecification(request);

        Page<AppointmentDetails> appointmentDetailsPage = appointmentDetailsRepository.findAll(spec, pageable);

        return appointmentDetailsPage;
    }

    private Integer generateInvoiceNo() {
        Integer generatedInvoiceNo = 1;
        Integer maxInvoiceNo = appointmentInvoiceRepository.fetchMaxInvoiceNo();
        if (hasValue(maxInvoiceNo)) {
            generatedInvoiceNo = maxInvoiceNo + 1;
        }
        return generatedInvoiceNo;
    }


    private Currency getCurrency(AppointmentDetails appointmentDetails) {
        Currency currency = null;
        for (AppointmentRequestedServices services : appointmentDetails.getAppointmentRequestedServices()) {
            for (AppointmentRequestedServiceCategories category : services.getAppointmentRequestedServiceCategories()) {
                for (AppointmentRequestedServiceSubCategories subcategory : category.getAppointmentRequestedServiceSubCategories()) {
                    currency = subcategory.getServiceItemSubCategory().getCurrency();
                    break;
                }
                if (hasValue(currency)) {
                    break;
                }
            }
            if (hasValue(currency)) {
                break;
            }
        }

        return currency;
    }
}
