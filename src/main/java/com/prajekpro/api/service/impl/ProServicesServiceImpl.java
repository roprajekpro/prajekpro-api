package com.prajekpro.api.service.impl;

import com.prajekpro.api.converters.*;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.repository.*;
import com.safalyatech.common.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;

import javax.servlet.http.*;
import javax.transaction.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class ProServicesServiceImpl implements ProServicesService {

    @Value("${file.upload.path}")
    private String fileUploadPath;

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private DTOFactory dtoService;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private ProService proService;

    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private ProDetailsRepository proDetailsRepository;
    @Autowired
    private PPLookupRepository ppLookUpRepository;
    @Autowired
    private ServiceItemCategoryRepository serviceItemCategoryRepository;
    @Autowired
    private ServiceItemSubCategoryRepository serviceItemSubCategoryRepository;
    @Autowired
    private ProServiceTimeSlotsRepository proServiceTimeSlotsRepository;
    @Autowired
    private ProAvailableDayRepository proAvailableDayRepository;
    @Autowired
    private TimeSlotsRepository timeSlotsRepository;
    @Autowired
    private ProServiceItemsPricingRepository proServiceItemsPricingRepository;
    @Autowired
    private ProCancellationTimeRepository proCancellationTimeRepository;
    @Autowired
    private ProServiceDocumentsRepository proServiceDocumentsRepository;
    @Autowired
    private ProServicesRepository proServicesRepository;
    @Autowired
    private ProServiceCommentsRepository proServiceCommentsRepository;
    @Autowired
    private ProServiceUnavailableDatesRepository proServiceUnavailableDatesRepository;

    @Override
    public BaseWrapper storeProSchedule(ProServiceScheduleDTO request, Long serviceId) throws ServicesException {
//TODO:not implement as per JPA 
        if (!hasValue(request) && !hasValue(serviceId)
                && request.getAvailableDays().isEmpty()
                && request.getAvailableTimeSlots().isEmpty()) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());

        if (!hasValue(proDetails)) {
            throw new ServicesException(GeneralErrorCodes.ERR_USER_NOT_REGISTERED.value());
        }
        Long proId = proDetails.getId();
        Services service = servicesRepository.findById(serviceId).get();

        List<ProServiceTimeSlots> proServiceTimeSlots = new ArrayList<>();
        for (CommonFieldsDTO timeSlot : request.getAvailableTimeSlots()) {
            TimeSlots timeSlots = new TimeSlots(timeSlot.getId(), timeSlot.getValue());
            ProServiceTimeSlots serviceTimeSlots = new ProServiceTimeSlots(proDetails, service, timeSlots);
            serviceTimeSlots.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            proServiceTimeSlots.add(serviceTimeSlots);
        }

        List<ProAvailableDay> proAvailableDays = new ArrayList<>();
        for (CommonFieldsDTO availableDay : request.getAvailableDays()) {
            PPLookUp day = new PPLookUp(availableDay.getId());
            ProAvailableDay proAvailableDay = new ProAvailableDay(proDetails, service, day);
            proAvailableDay.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            proAvailableDays.add(proAvailableDay);
        }
        int timeDeleted = proServiceTimeSlotsRepository.deleteByProIdAndServiceId(proId, serviceId);
        int dayDeleted = proAvailableDayRepository.deleteByProAndService(proId, serviceId);

        proServiceTimeSlotsRepository.saveAll(proServiceTimeSlots);
        proAvailableDayRepository.saveAll(proAvailableDays);
        return new BaseWrapper(request);
    }

    @Override
    public BaseWrapper getProServiceSchedule(Long serviceId) throws ServicesException {

        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());
        Long proId = proDetails.getId();

        List<Long> proAvailableDaysId = proAvailableDayRepository.fetchByProIdAndServiceId(proId, serviceId);
        log.debug("pro available days  = {}", proAvailableDaysId.toString());
        List<String> reference = Arrays.asList("day");
        List<PPLookUp> days = ppLookUpRepository.findByReferenceIn(reference);

        List<TimeSlots> timeSlots = timeSlotsRepository.findByActiveStatus(ActiveStatus.ACTIVE.value());
        List<Long> proServiceTimeSlots = proServiceTimeSlotsRepository.fetchByProIdAndServiceId(proId, serviceId);
        log.debug("pro time slots = {}", proServiceTimeSlots);
        ProServiceScheduleDTO proScheduleResponse = new ProServiceScheduleDTO(proAvailableDaysId, days, proServiceTimeSlots, timeSlots);

        return new BaseWrapper(proScheduleResponse);
    }

    @Override
    public void updateProServices(Long proId, List<Services> request) throws ServicesException {
        ProDetails proDetails = validateProId(proId, request);

        for (Services services : request) {
            System.out.println("ID - " + services.getId() + ", isCertified - " + services.isCertified());
        }

        //Fetch All existing PRO services
        List<ProServices> existingProServices = proServicesRepository.findByProId(proId);

        //For requested services with `isCertified=true` check if required documents exists or not and throw appropriate exception
//        Set<Long> certifiedServiceIds = request.stream().filter(Services::isCertified).map(Services::getId).collect(Collectors.toSet());
//        System.out.println("certifiedServiceIds = " + certifiedServiceIds);
//        if (hasValue(certifiedServiceIds) && certifiedServiceIds.size() > 0) {
//            List<ProServiceDocuments> proServiceDocuments = proServiceDocumentsRepository.findByProDetails_IdAndDocTypeAndService_IdIn(proId, DocType.CERT_N_LICENSES, certifiedServiceIds);
//            if (!hasValue(proServiceDocuments) || proServiceDocuments.size() < certifiedServiceIds.size())
//                throw new ServicesException(709);
//        }

        List<ProServices> finalProServices = new ArrayList<>();
        Set<Long> existingServiceIds = new HashSet<>();
        final Map<Long, Boolean> requestServiceIdsToIsCertifiedMap = request.stream().collect(Collectors.toMap(Services::getId, Services::isCertified));
        System.out.println("requestServiceIdsToIsCertifiedMap - " + requestServiceIdsToIsCertifiedMap);
        int activeStatus = ActiveStatus.ACTIVE.value();
        int inactiveStatus = ActiveStatus.INACTIVE.value();
        int reviewStatus = ActiveStatus.REVIEW.value();
        List<Long> serviceIdToDeleteDocumentsFor = new ArrayList<>();
        for (ProServices proService : existingProServices) {
            Long serviceId = proService.getServiceId();

            existingServiceIds.add(serviceId);

            //If the existing PRO service is not present in requested services ID's then deactivate it.
            if (requestServiceIdsToIsCertifiedMap.containsKey(serviceId)) {
                if (proService.getActiveStatus() == ActiveStatus.INACTIVE.value()) {
                    proService.updateAuditableFields(false, GlobalConstants.USER_API, reviewStatus);
                }

                if (!requestServiceIdsToIsCertifiedMap.get(serviceId))
                    serviceIdToDeleteDocumentsFor.add(serviceId);
            } else {
                proService.updateAuditableFields(false, GlobalConstants.USER_API, inactiveStatus);
                serviceIdToDeleteDocumentsFor.add(serviceId);
            }

            proService.setCertified(hasValue(requestServiceIdsToIsCertifiedMap.get(serviceId)) ? requestServiceIdsToIsCertifiedMap.get(serviceId) : false);

            finalProServices.add(proService);
        }

        //Remove all existing service IDs from requested Service ID's
        existingServiceIds.forEach(sId -> requestServiceIdsToIsCertifiedMap.remove(sId));

        //Add all new services which are not present in existing services
        for (Services services : request) {
            Long serviceID = services.getId();
            if (requestServiceIdsToIsCertifiedMap.containsKey(serviceID)) {
                boolean isServiceIdCertified = requestServiceIdsToIsCertifiedMap.get(serviceID);
                //Delete the documents if the service is not selected as certified
                if (!isServiceIdCertified)
                    serviceIdToDeleteDocumentsFor.add(serviceID);

                //Create the new PRO service
                ProServices proService = new ProServices(proId, serviceID, isServiceIdCertified);
                proService.updateAuditableFields(true, GlobalConstants.USER_API, reviewStatus);

                finalProServices.add(proService);
            }
        }

        proServicesRepository.saveAll(finalProServices);

        List<ProServiceDocuments> proServiceDocumentsToDelete = proServiceDocumentsRepository.findByProDetails_IdAndService_IdIn(proId, serviceIdToDeleteDocumentsFor);
        proServiceDocumentsRepository.deleteAll(proServiceDocumentsToDelete);
    }


    @Override
    public BaseWrapper getProServices(Long proId) throws ServicesException {

//        Set<Services> proServices = proDetailsRepository.fetchServicesByProIdIn(proId);
        Set<Services> proServices = servicesRepository.fetchServicesByProIdIn(proId, Arrays.asList(ActiveStatus.ACTIVE.value()));
        return new BaseWrapper(new ProServiceDTOConverter().convert(proServices));
    }

    @Override
    public BaseWrapper getProSubCategoryPricing(Long serviceId) throws ServicesException {
        if (!hasValue(serviceId) || serviceId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }
        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());
        Long proId = proDetails.getId();

        List<ProServiceItemsPricing> proServiceItemsPricings = proServiceItemsPricingRepository.findByProDetails_IdAndServices_Id(proId, serviceId);

        Optional<Services> servicesOptional = servicesRepository.findById(serviceId);
        if (!servicesOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        Services services = servicesOptional.get();
        List<ServiceItemCategoriesDTO> serviceCategories = new ArrayList<>();

        List<ServiceItemCategory> serviceItemCategories = serviceItemCategoryRepository.findByServices_IdAndActiveStatus(serviceId, ActiveStatus.ACTIVE.value());
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
            if (!serviceItemSubCategories.isEmpty()) {
                category.setServiceItemSubCategoryList(serviceSubCategories);
                serviceCategories.add(category);
            }
        }

        ProCancellationTime proCancellationTime = proCancellationTimeRepository.fetchTimeByProIdAndServiceId(proId, serviceId);
        ProCancellationTimeDTO proCancellationTimeDTO = null;
        if (!hasValue(proCancellationTime)) {
            proCancellationTimeDTO = new ProCancellationTimeDTO(services);
        } else {
            proCancellationTimeDTO = new ProCancellationTimeDTO(proCancellationTime);
        }
        proCancellationTimeDTO.setCancellationFees(services.getCancellationFees());
        proCancellationTimeDTO.setCancellationFeesUnit(services.getCancellationFeesUnit().getValue());
        proCancellationTimeDTO.setCancellationFeesUnitId(services.getCancellationFeesUnit().getId());

        ProServiceListDTO proServiceList = new ProServiceListDTO(services, serviceCategories, proCancellationTimeDTO);

        return new BaseWrapper(proServiceList);
    }

    @Override
    public BaseWrapper storeProSubCategoryPrice(Long serviceId, ProServiceListDTO request) throws ServicesException {
        if (!hasValue(serviceId) || serviceId <= 0 || !hasValue(request)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());
        Long proId = proDetails.getId();
        List<Long> servicePricingId = new ArrayList<>();

        Optional<Services> servicesOptional = servicesRepository.findById(serviceId);
        if (!servicesOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        Services services = servicesOptional.get();
        List<Float> servicePrice = new ArrayList<>();
        for (ServiceItemCategoriesDTO category : request.getServiceCategories()) {
            for (ServiceItemSubCategoryDTO subcategory : category.getServiceItemSubCategoryList()) {
                servicePricingId.add(subcategory.getProServicePricingId());
                servicePrice.add(subcategory.getProSubCategoryPrice());
            }
        }

        // remove zero id's
        servicePricingId = servicePricingId.stream().filter(value -> value > 0).collect(Collectors.toList());

        servicePrice = servicePrice.stream().filter(value -> value > 0).collect(Collectors.toList());
        Long startingPrice = Long.valueOf(Math.round(Collections.min(servicePrice)));
        /* proDetails.setStartingCost(startingPrice);*/

        proDetailsRepository.save(proDetails);
        log.debug("starting price ={} added in pro={}", startingPrice, proId);

        // delete which are not in list
        int rowsUpdated = proServiceItemsPricingRepository.deleteByIdNotInANDProDetails(servicePricingId, proId, serviceId);
        log.debug("rowsUpdated = {}", rowsUpdated);

        // save the given request
        List<ProServiceItemsPricing> proServiceItemsPricings = new ArrayList<>();
        for (ServiceItemCategoriesDTO category : request.getServiceCategories()) {
            for (ServiceItemSubCategoryDTO subcategory : category.getServiceItemSubCategoryList()) {
                ProServiceItemsPricing servicePricing = new ProServiceItemsPricing();
                if (hasValue(subcategory.getProServicePricingId()) || subcategory.getProServicePricingId() != 0) {
                    servicePricing.setId(subcategory.getProServicePricingId());
                }
                servicePricing.setProDetails(proDetails);
                servicePricing.setServices(services);
                servicePricing.setPrice(subcategory.getProSubCategoryPrice());
                ServiceItemSubCategory serviceItemSubCategory = new ServiceItemSubCategory(subcategory.getId());
                servicePricing.setServiceItemSubcategory(serviceItemSubCategory);
                servicePricing.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
                //TODO: set currency is remaining
                servicePricing.setCurrency(subcategory.getCurrency());
                proServiceItemsPricings.add(servicePricing);
            }
        }

        proServiceItemsPricingRepository.saveAll(proServiceItemsPricings);
        BaseWrapper response = getProSubCategoryPricing(serviceId);
        return response;
    }

    @Override
    public BaseWrapper deleteSubCategoryPrice(Long serviceId, List<Long> requestId) throws ServicesException {
        if (!hasValue(serviceId) || serviceId <= 0 || !hasValue(requestId)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());
        Long proId = proDetails.getId();

        int rowsUpdated = proServiceItemsPricingRepository.deleteRecordByIds(requestId);

        return new BaseWrapper(rowsUpdated);
    }

    @Override
    public BaseWrapper updateProCancellationTime(ProCancellationTimeDTO request, HttpServletRequest servletRequest) throws ServicesException {
        if (!hasValue(request)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Users user = authorizationService.fetchLoggedInUser();
        ProDetails proDetails = proDetailsRepository.findByUserDetails_UserId(user.getUserId());

        Long serviceId = request.getServiceId();
        Long cancellationTimeUnitId = request.getCancellationTimeUnitId();
        Services service = servicesRepository.findById(serviceId).get();
        List<String> reference = Arrays.asList("cancellationUnit");

        List<PPLookUp> referenceForTimeUnit = ppLookUpRepository.findByReferenceIn(reference);
        Long serviceCancellationTimeInHours = service.getCancellationTime();
        Long requestCancellationTimeInHours = request.getCancellationTime();

        for (PPLookUp unit : referenceForTimeUnit) {
            if (unit.getValue().equalsIgnoreCase("Days")) {
                if (unit.getId() == service.getCancellationTimeUnit().getId()) {
                    serviceCancellationTimeInHours = serviceCancellationTimeInHours * 24;
                }
                if (unit.getId() == request.getCancellationTimeUnitId()) {
                    requestCancellationTimeInHours = requestCancellationTimeInHours * 24;
                }
            }
        }

        if (requestCancellationTimeInHours > serviceCancellationTimeInHours) {

            List<String> requestAttributes = new ArrayList<>();
            requestAttributes.add(Long.toString(service.getCancellationTime()));
            requestAttributes.add(service.getCancellationTimeUnit().getValue());
            new ExceptionUtility().setServletRequest(servletRequest, requestAttributes);

            throw new ServicesException(GeneralErrorCodes.ERR_CANCELLATION_TIME_EXCEEDS.value());
        }

        ProCancellationTime proCancellationTime = new ProCancellationTime(proDetails, request);
        proCancellationTime.updateAuditableFields(true, user.getEmailId(), ActiveStatus.ACTIVE.value());
        if (hasValue(request.getProCancellationTimeId())) {
            proCancellationTime.setId(request.getProCancellationTimeId());
            proCancellationTime.updateAuditableFields(false, user.getEmailId(), ActiveStatus.ACTIVE.value());
        }

        proCancellationTimeRepository.save(proCancellationTime);
        request.setProCancellationTimeId(proCancellationTime.getId());
        return new BaseWrapper(request);
    }

    @Override
    public BaseWrapper deleteProServices(Long proId, Long serviceId) throws ServicesException {
        if (!hasValue(serviceId) || !hasValue(proId) || proId == 0 || serviceId == 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }
        ProDetails proDetails = getProDetailsFromOptional(proId);
        Set<Services> existingProServices = proDetails.getProServices();

        Services services = new Services();
        services.setId(serviceId);
        services.updateAuditableFields(true, GlobalConstants.USER_API, ActiveStatus.ACTIVE.value());


        List<Long> serviceIds = Arrays.asList(serviceId);
        proServiceItemsPricingRepository.deleteByServiceAndProDetails(serviceIds, proId);
        log.debug("deleted subcategories for service = {}", serviceIds);

        existingProServices.remove(services);
        proDetails.setProServices(existingProServices);
        proDetailsRepository.save(proDetails);
        log.debug("pro services list count ={}", existingProServices.stream().count());

        return new BaseWrapper(serviceId);
    }

    @Override
    public BaseWrapper uploadProServiceDocument(Long proId, Long serviceId, DocType docType, MultipartFile file) throws ServicesException, IOException {

        //Get any existing records
//        Optional<ProServiceDocuments> proServiceDocumentOptional = proServiceDocumentsRepository.findByProDetails_IdAndService_Id(proId, serviceId);
        ProServiceDocuments proServiceDocuments = null;
        boolean isCreate = false;
//        if (proServiceDocumentOptional.isPresent())
//            proServiceDocuments = proServiceDocumentOptional.get();
//        else {
//            proServiceDocuments = new ProServiceDocuments(new ProDetails(proId), new Services(serviceId));
//            isCreate = true;
//        }
        proServiceDocuments = new ProServiceDocuments(new ProDetails(proId), new Services(serviceId));
        isCreate = true;

        //Upload passed service documents
        FileDetailsDTO fileDetailsDTO = fileUploadService.transferFile(file, fileUploadPath);

        //Update file transfer details and docType in pro service documents
        proServiceDocuments.updateFileDetails(fileDetailsDTO);
        proServiceDocuments.setDocType(docType);
        proServiceDocuments.setUploadDt(System.currentTimeMillis());
        proServiceDocuments.updateAuditableFields(isCreate, GlobalConstants.StringConstants.DEFAULT_USER_EMAIL_ID, ActiveStatus.REVIEW.value());

        //Save pro_service_documents
        proServiceDocumentsRepository.save(proServiceDocuments);

        //Prepare response and send
        ProServiceDocumentsDTO proServiceDocumentsDTO = new ProServiceDocumentsDTO(proServiceDocuments);
        return new BaseWrapper(proServiceDocumentsDTO);
    }

    @Override
    public BaseWrapper updateProService(Long proId, Long serviceId, ProServiceUpdateDTO request) throws ServicesException {
        ActiveStatus activeStatus = request.getActiveStatus();
        if (!hasValue(proId) || !hasValue(serviceId) || !hasValue(activeStatus))
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

        //Check if for given PRO Id and Service ID, there is atleast 1 document uploaded in case user wants to activate the PRO service
        if (activeStatus == ActiveStatus.ACTIVE) {
            if (!request.isCertified() && !request.isPrajekProVerified())
                throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());

            List<ProServiceDocuments> proServiceDocuments = proServiceDocumentsRepository.findByProDetails_IdAndService_Id(proId, serviceId, null);
            if (!hasValue(proServiceDocuments))
                throw new ServicesException(706);
            //Check if activation is for CertifiedNLicense then the PRO should have atleast 1 certificateNLicense Doc uploaded
            boolean isCertifiedDocAvailable = false;
            boolean isPrajeproVerifiedDocAvailable = false;
            for (ProServiceDocuments proServiceDocuments1 : proServiceDocuments) {
                if (proServiceDocuments1.getDocType() == DocType.CERT_N_LICENSES)
                    isCertifiedDocAvailable = true;
                if (proServiceDocuments1.getDocType() == DocType.PRAJEPRO_RESULTS)
                    isPrajeproVerifiedDocAvailable = true;
            }

            if ((request.isCertified() && !isCertifiedDocAvailable)
                    || (request.isPrajekProVerified() && !isPrajeproVerifiedDocAvailable))
                throw new ServicesException(709);
        }

        Optional<ProServices> proServicesOptional = proServicesRepository.findByProIdAndServiceId(proId, serviceId);
        if (!proServicesOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        ProServices proServices = proServicesOptional.get();
        proServices.setActiveStatus(activeStatus.value());
        if (activeStatus == ActiveStatus.ACTIVE) {
            proServices.setCertified(request.isCertified());
            proServices.setPrajekproVerified(request.isPrajekProVerified());
        }
        proServicesRepository.save(proServices);

        if (activeStatus == ActiveStatus.ACTIVE &&
                hasValue(request.getComment()) &&
                hasValue(request.getComment().getSubject()) &&
                hasValue(request.getComment().getComment())) {
            BaseWrapper commentBaseWrapper = updateProComment(proId, serviceId, new ProServiceCommentsDTO(request.getComment().getSubject(), request.getComment().getComment()));
            request.setComment((ProServiceCommentsDTO) commentBaseWrapper.getResponse());
        }

        return new BaseWrapper(request);
    }

    @Override
    public DownloadImageDTO getProServiceDocDetails(String docId) throws ServicesException {
        Optional<ProServiceDocuments> proServiceDocumentsOpt = proServiceDocumentsRepository.findById(Long.parseLong(docId));
        if (!proServiceDocumentsOpt.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        ProServiceDocuments proServiceDocuments = proServiceDocumentsOpt.get();
        DownloadImageDTO downloadImageDTO = new DownloadImageDTO();
        downloadImageDTO.setImgExtn(proServiceDocuments.getFileExtension());
        downloadImageDTO.setDisplayName(proServiceDocuments.getFileName());
        String fullFilePath = proServiceDocuments.getFilePath() + proServiceDocuments.getFileSavedName();
        downloadImageDTO.setFilePath(fullFilePath);

        return downloadImageDTO;
    }

    @Override
    public BaseWrapper updateProComment(Long proId, Long serviceId, ProServiceCommentsDTO request) throws ServicesException {
        //Validate request data
        if (!hasValue(request)
                || !hasValue(request.getSubject())
                || !hasValue(request.getComment())
                || !hasValue(proId)
                || !hasValue(serviceId))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        //Validate Comment character length
        String comment = request.getComment();
        if (comment.length() > GlobalConstants.MAX_LONG_TEXT_LENGTH)
            throw new ServicesException(707);
        //Validate Subject character length
        String subject = request.getSubject();
        if (subject.length() > GlobalConstants.MAX_SHORT_TEXT_LENGTH)
            throw new ServicesException(708);

        //Save the comment
        ProServiceComments proServiceComment = new ProServiceComments(proId, serviceId, comment, subject);
        proServiceComment.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());
        proServiceCommentsRepository.save(proServiceComment);

        return new BaseWrapper(new ProServiceCommentsConverter().convert(proServiceComment));
    }

    @Override
    public BaseWrapper getProServiceComments(Long proId, Long serviceId) throws ServicesException {
        CommonUtility.validateInput(proId, serviceId);

        List<ProServiceComments> proServiceComments = proServiceCommentsRepository.findByProDetails_IdAndService_IdAndActiveStatusOrderByModifiedTsDesc(proId, serviceId, ActiveStatus.ACTIVE.value());
        if (!hasValue(proServiceComments))
            return new BaseWrapper(new ArrayList<>());
        else
            return new BaseWrapper(new ProServiceCommentsConverter().convert(proServiceComments));
    }

    @Override
    public BaseWrapper getProServiceUnavailableDates(Long proId, Long serviceId) throws ServicesException {
        CommonUtility.validateInput(proId, serviceId);

        List<ProServiceUnavailableDates> proServiceUnavailableDates = proServiceUnavailableDatesRepository.findByProDetails_IdAndService_IdAndActiveStatusOrderByStartDtAsc(proId, serviceId, ActiveStatus.ACTIVE.value());

        if (!hasValue(proServiceUnavailableDates))
            return new BaseWrapper(new ArrayList<>());
        else
            return new BaseWrapper(new ProServiceUnavailableDatesConverter().convert(proServiceUnavailableDates));
    }

    @Override
    public BaseWrapper addProServiceUnavailableDate(Long proId, Long serviceId, ProServiceUnavailableDates request) throws ServicesException {
        CommonUtility.validateInput(proId, serviceId, request);

        ProServiceUnavailableDates proServiceUnavailableDate = proServiceUnavailableDatesRepository.findByProDetails_IdAndService_IdAndStartDtAndEndDt(proId, serviceId, request.getStartDt(), request.getEndDt());

        boolean isCreate = true;
        if (hasValue(proServiceUnavailableDate)) {
            isCreate = false;
        } else {
            proServiceUnavailableDate = new ProServiceUnavailableDates(proId, serviceId, request);
        }
        proServiceUnavailableDate.updateAuditableFields(isCreate, authorizationService.fetchLoggedInUser().getUserId(), ActiveStatus.ACTIVE.value());
        proServiceUnavailableDatesRepository.save(proServiceUnavailableDate);

        return getProServiceUnavailableDates(proId, serviceId);
    }

    @Override
    public BaseWrapper deleteProServiceUnavailableDate(Long proId, Long serviceId, Long unavailableDateId) throws ServicesException {
        CommonUtility.validateInput(proId, serviceId, unavailableDateId);

        Optional<ProServiceUnavailableDates> proServiceUnavailableDatesOptional = proServiceUnavailableDatesRepository.findById(unavailableDateId);
        if (!proServiceUnavailableDatesOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        ProServiceUnavailableDates proServiceUnavailableDate = proServiceUnavailableDatesOptional.get();
        proServiceUnavailableDate.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getUserId(), ActiveStatus.INACTIVE.value());
        proServiceUnavailableDatesRepository.save(proServiceUnavailableDate);

        return new BaseWrapper();
    }

    @Override
    public BaseWrapper deleteUploadProServiceDocument(Long proId, Long serviceId, Long docId) throws ServicesException {
        log.debug("proId = {}, serviceId = {}, docId = {}", proId, serviceId, docId);
        CommonUtility.validateInput(proId, serviceId, docId);

        if (proServiceDocumentsRepository.existsById(docId))
            proServiceDocumentsRepository.deleteById(docId);

        Integer certAndLicenseProDocsCount = proServiceDocumentsRepository.countByProDetails_IdAndService_IdAndDocTypeAndActiveStatus(proId, serviceId, DocType.CERT_N_LICENSES, ActiveStatus.ACTIVE.value());
        log.debug("certAndLicenseProDocsCount = {}", certAndLicenseProDocsCount);
        if (!hasValue(certAndLicenseProDocsCount) || certAndLicenseProDocsCount <= 0)
            proServicesRepository.updateIsCertifiedByProIdAndServiceIdAndActiveStatus(false, proId, serviceId, ActiveStatus.ACTIVE.value());

        return new BaseWrapper(docId);
    }

    private class ProServiceUnavailableDatesConverter extends DTOConverter<ProServiceUnavailableDates, ProServiceUnavailableDatesDTO> {
        @Override
        public ProServiceUnavailableDatesDTO convert(ProServiceUnavailableDates input) {
            return new ProServiceUnavailableDatesDTO(input);
        }
    }

    private class ProServiceDTOConverter extends DTOConverter<Services, ProServicesDTO> {
        @Override
        public ProServicesDTO convert(Services input) {
            return dtoService.createProServiceDTO(input);
        }
    }

    private class ProServiceCommentsConverter extends DTOConverter<ProServiceComments, ProServiceCommentsDTO> {
        @Override
        public ProServiceCommentsDTO convert(ProServiceComments input) {
            return dtoService.createProServiceCommentsDTO(input);
        }

    }

    private ProDetails validateProId(Long proId, Collection<?> request) throws ServicesException {
        if (!hasValue(request) || !hasValue(proId) || proId <= 0)
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        return getProDetailsFromOptional(proId);
    }


    private ProDetails getProDetailsFromOptional(Long proId) throws ServicesException {

        Optional<ProDetails> proDetailsOptional = proDetailsRepository.findById(proId);
        if (!proDetailsOptional.isPresent())
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        return proDetailsOptional.get();
    }
}
