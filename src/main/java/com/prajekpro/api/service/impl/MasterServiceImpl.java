package com.prajekpro.api.service.impl;

import com.prajekpro.api.domain.Currency;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.repository.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.enums.*;
import com.safalyatech.common.exception.*;
import com.safalyatech.common.utility.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;

import javax.transaction.*;
import java.io.*;
import java.util.*;

@Slf4j
@Service
@Transactional(rollbackOn = Throwable.class)
public class MasterServiceImpl implements MasterService {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private ServicesRepository servicesRepository;
    @Autowired
    private ServiceItemCategoryRepository serviceItemCategoryRepository;
    @Autowired
    private ServiceItemSubCategoryRepository serviceItemSubCategoryRepository;
    @Autowired
    private TimeSlotsRepository appointmentTimeSlotsRepository;
    @Autowired
    private AdvertisementsRepository advertisementsRepository;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private AdvertisementImageRepository advertisementImageRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    @Value("${file.upload.path}")
    private String fileUploadPath;

    // To display list of services
    @Override
    public BaseWrapper getListOfServices(Source source, Pageable pageable) throws ServicesException {
        if (!CheckUtil.hasValue(source)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Page<Services> servicePage = null;
        if (source.name().equals(Source.WEB.name())) {
            servicePage = servicesRepository.findAllByOrderByActiveStatus(pageable);
        }
        if (source.name().equals(Source.APP.name())) {
            servicePage = servicesRepository.findAllByActiveStatus(ActiveStatus.ACTIVE.value(), pageable);
        }
        //Page<Services> servicePage = servicesRepository.findAll(pageable);
        if (!servicePage.hasContent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        List<Services> servicesList = servicePage.getContent();
        List<ServicesDTO> servicesDTOList = new ArrayList<>();
        for (Services service : servicesList) {
            servicesDTOList.add(new ServicesDTO(service));
        }
        Collections.sort(servicesDTOList, Comparator.comparing(ServicesDTO::getIsActive).reversed());
        Pagination pagination = new Pagination(servicesList, servicePage.getTotalElements(), pageable);
        return new BaseWrapper(servicesDTOList, pagination);
    }

    // To Add new Service
    @Override
    public BaseWrapper addServices(ServicesDTO request) throws ServicesException {
        //TODO: Add JSR validations
        if (!CheckUtil.hasValue(request)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Services service = new Services();
        service.setServiceIcon(request.getServiceIcon());
        service.setServiceName(request.getServiceName());
        service.setSortOrder(request.getSortOrder());
        service.setDescription(request.getDescription());
        service.setCancellationTime(request.getCancellationTime());
        service.setCancellationFees(request.getCancellationFees());
        PPLookUp cancellationTimeUnit = new PPLookUp(request.getCancellationTimeUnitId());
        PPLookUp cancellationFeesUnit = new PPLookUp(request.getCancellationFeesUnitId());
        service.setCancellationFeesUnit(cancellationFeesUnit);
        service.setCancellationTimeUnit(cancellationTimeUnit);

        System.out.println("Logged In User Email - " + authorizationService.fetchLoggedInUser().getEmailId());
        service.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
        servicesRepository.save(service);
        request.setId(service.getId());
        return new BaseWrapper(request);
    }

    // To update service
    @Override
    public BaseWrapper editServices(ServicesDTO request) throws ServicesException {
        Long serviceId = request.getId();
        // check whether service id present or not and it is neither null nor zero
        if (!CheckUtil.hasValue(serviceId) || serviceId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Optional<Services> servicesOptional = servicesRepository.findById(serviceId);
        // update db service record with new data
        if (servicesOptional.isPresent()) {
            Services service = servicesOptional.get();
            service.setId(request.getId());
            service.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            service.setServiceIcon(request.getServiceIcon());
            service.setServiceName(request.getServiceName());
            service.setSortOrder(request.getSortOrder());
            service.setDescription(request.getDescription());
            service.setCancellationTime(request.getCancellationTime());
            service.setCancellationFees(request.getCancellationFees());
            PPLookUp cancellationTimeUnit = new PPLookUp(request.getCancellationTimeUnitId());
            PPLookUp cancellationFeesUnit = new PPLookUp(request.getCancellationFeesUnitId());
            service.setCancellationFeesUnit(cancellationFeesUnit);
            service.setCancellationTimeUnit(cancellationTimeUnit);
            servicesRepository.save(service);
            return new BaseWrapper(request);
        } else {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

    }

    //To delete service
    @Override
    public BaseWrapper deleteService(Long id, SingleValue<Integer> isActive) throws ServicesException {

        if (!CheckUtil.hasValue(id) || id <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        if (!CheckUtil.hasValue(isActive) || isActive.getValue() < 0 || isActive.getValue() > 1) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }
        int activeStatus = isActive.getValue();
        Optional<Services> servicesOptional = servicesRepository.findById(id);
        if (!servicesOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        Services service = servicesOptional.get();
        if (activeStatus == 0) {
            service.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.INACTIVE.value());
        } else {
            service.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
        }

        servicesRepository.save(service);
        return new BaseWrapper(service.getId());

    }

    // To Display service Categories
    @Override
    public BaseWrapper getListOfServiceCategories(Source source, Long id, Pageable pageable) throws ServicesException {
        if (!CheckUtil.hasValue(source)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Page<ServiceItemCategory> serviceCategoryPage = null;
        if (source.name().equals(Source.WEB.name())) {
            serviceCategoryPage = serviceItemCategoryRepository.findByServices_IdOrderByActiveStatus(id, pageable);
        }
        if (source.name().equals(Source.APP.name())) {
            serviceCategoryPage = serviceItemCategoryRepository.findByServices_IdAndActiveStatus(id, ActiveStatus.ACTIVE.value(), pageable);

        }
        if (!serviceCategoryPage.hasContent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        List<ServiceItemCategory> serviceCategoryList = serviceCategoryPage.getContent();

        List<ServiceItemCategoriesDTO> serviceItemCategoriesDTOS = new ArrayList<ServiceItemCategoriesDTO>();

        serviceCategoryList.forEach(
                serviceItemCategory ->
                        serviceItemCategoriesDTOS.add(new ServiceItemCategoriesDTO(serviceItemCategory)));

        Collections.sort(serviceItemCategoriesDTOS, Comparator.comparing(ServiceItemCategoriesDTO::getIsActive).reversed());
        Pagination pagination = new Pagination(serviceItemCategoriesDTOS, serviceCategoryPage.getTotalElements(), pageable);
        return new BaseWrapper(serviceItemCategoriesDTOS, pagination);
    }


    // to save new service Category
    @Override
    public BaseWrapper addServiceCategory(ServiceItemCategoriesDTO request, Long id) {

        ServiceItemCategory serviceItemCategory = new ServiceItemCategory();
        serviceItemCategory.setValue(request.getDisplayValue());
        serviceItemCategory.setReference(request.getStoredValue());
        serviceItemCategory.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
        Services services = new Services(id);
        serviceItemCategory.setServices(services);
        serviceItemCategoryRepository.save(serviceItemCategory);
        request.setId(serviceItemCategory.getId());
        return new BaseWrapper(request);
    }

    // to update already present record(service category)
    @Override
    public BaseWrapper updateServiceCategory(ServiceItemCategoriesDTO request, Long id) throws ServicesException {

        Long serviceCategoryId = request.getId();

        if (!CheckUtil.hasValue(serviceCategoryId) || serviceCategoryId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Optional<ServiceItemCategory> serviceItemCategoryoptional = serviceItemCategoryRepository.findById(serviceCategoryId);
        System.out.println("ID = " + serviceCategoryId + ", serviceItemCategoryoptional.isPresent() = " + serviceItemCategoryoptional.isPresent());
        if (serviceItemCategoryoptional.isPresent()) {
            ServiceItemCategory serviceItemCategory = serviceItemCategoryoptional.get();
            serviceItemCategory.setReference(request.getStoredValue());
            serviceItemCategory.setValue(request.getDisplayValue());
            serviceItemCategory.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            Services services = new Services(id);
            serviceItemCategory.setServices(services);
            serviceItemCategoryRepository.save(serviceItemCategory);
            ServiceItemCategoriesDTO category = new ServiceItemCategoriesDTO(serviceItemCategory);
            return new BaseWrapper(category);
        } else {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
    }

    //to delete service category
    @Override
    public BaseWrapper deleteServiceCategory(Long id, Long categoryId, SingleValue<Integer> isActive) throws ServicesException {
        if (!CheckUtil.hasValue(categoryId) || categoryId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        if (!CheckUtil.hasValue(isActive) || isActive.getValue() < 0 || isActive.getValue() > 1) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        Optional<ServiceItemCategory> servicesCategoryOptional = serviceItemCategoryRepository.findById(categoryId);
        if (!servicesCategoryOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        ServiceItemCategory serviceItemCategory = servicesCategoryOptional.get();
        if (isActive.getValue() == 0) {
            serviceItemCategory.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.INACTIVE.value());
        } else {
            serviceItemCategory.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
        }
        serviceItemCategoryRepository.save(serviceItemCategory);
        return new BaseWrapper(serviceItemCategory.getId());
    }

    // To Display list of service subcategory
    @Override
    public BaseWrapper getListOfServiceSubcategory(Source source, Long id, Long categoryId, Pageable pageable) throws ServicesException {
        if (!CheckUtil.hasValue(source)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Page<ServiceItemSubCategory> serviceItemSubCategoryPage = null;
        if (source.name().equals(Source.WEB.name())) {
            serviceItemSubCategoryPage = serviceItemSubCategoryRepository.findByServiceItemCategory_IdOrderByActiveStatus(categoryId, pageable);
        }
        if (source.name().equals(Source.APP.name())) {
            serviceItemSubCategoryPage = serviceItemSubCategoryRepository.findByServiceItemCategory_IdAndActiveStatus(categoryId, ActiveStatus.ACTIVE.value(), pageable);

        }
        if (!CheckUtil.hasValue(categoryId) || categoryId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        if (!serviceItemSubCategoryPage.hasContent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        List<ServiceItemSubCategory> serviceItemSubCategoryList = serviceItemSubCategoryPage.getContent();
        List<ServiceItemSubCategoryDTO> serviceItemSubCategoryDTOList = new ArrayList<ServiceItemSubCategoryDTO>();
        serviceItemSubCategoryList.forEach(
                serviceItemSubCategory ->
                        serviceItemSubCategoryDTOList.add(new ServiceItemSubCategoryDTO(serviceItemSubCategory)));

        //Sort Response by isActive
        Collections.sort(serviceItemSubCategoryDTOList, Comparator.comparing(ServiceItemSubCategoryDTO::getIsActive).reversed());

        Pagination pagination = new Pagination(serviceItemSubCategoryDTOList, serviceItemSubCategoryPage.getTotalElements(), pageable);
        return new BaseWrapper(serviceItemSubCategoryDTOList, pagination);
    }

    // add new service sub-category
    @Override
    public BaseWrapper addServiceSubCategory(ServiceItemSubCategoryDTO request, Long id, Long categoryId) throws ServicesException {

        if (!CheckUtil.hasValue(request) || !CheckUtil.hasValue(categoryId) || categoryId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        ServiceItemSubCategory serviceItemSubCategory = new ServiceItemSubCategory();
        serviceItemSubCategory.setItemSubCategoryName(request.getSubCategoryName());
        serviceItemSubCategory.setItemSubCategoryDesc(request.getSubCategoryDesc());
        serviceItemSubCategory.setDefaultFillipinoPrice(request.getDefaultFillipinoPrice());
        Currency currency = new Currency(request.getCurrencyId());
        serviceItemSubCategory.setCurrency(currency);
        serviceItemSubCategory.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
        ServiceItemCategory serviceItemCategory = new ServiceItemCategory(categoryId);
        serviceItemSubCategory.setServiceItemCategory(serviceItemCategory);

        serviceItemSubCategoryRepository.save(serviceItemSubCategory);
        ServiceItemSubCategoryDTO subCategory = new ServiceItemSubCategoryDTO(serviceItemSubCategory);

        return new BaseWrapper(subCategory);
    }

    // update service sub-category
    @Override
    public BaseWrapper updateServiceSubCategory(ServiceItemSubCategoryDTO request, Long id, Long categoryId) throws ServicesException {
        Long serviceSubCategoryId = request.getId();

        if (!CheckUtil.hasValue(serviceSubCategoryId) || serviceSubCategoryId <= 0 || !CheckUtil.hasValue(request)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Optional<ServiceItemSubCategory> serviceItemSubCategoryOptional = serviceItemSubCategoryRepository.findById(serviceSubCategoryId);
        if (serviceItemSubCategoryOptional.isPresent()) {
            ServiceItemSubCategory serviceItemSubCategory = serviceItemSubCategoryOptional.get();
            serviceItemSubCategory.setItemSubCategoryName(request.getSubCategoryName());
            serviceItemSubCategory.setItemSubCategoryDesc(request.getSubCategoryDesc());
            serviceItemSubCategory.setDefaultFillipinoPrice(request.getDefaultFillipinoPrice());
            serviceItemSubCategory.setParentId(request.getParentId());
            Currency currency = new Currency(request.getCurrencyId());
            serviceItemSubCategory.setCurrency(currency);
            serviceItemSubCategory.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            ServiceItemCategory serviceItemCategory = new ServiceItemCategory(categoryId);
            serviceItemSubCategory.setServiceItemCategory(serviceItemCategory);
            serviceItemSubCategoryRepository.save(serviceItemSubCategory);

            return new BaseWrapper(request);
        } else {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
    }

    // delete service sub-category
    @Override
    public BaseWrapper deleteServiceSubCategory(Long id, Long categoryId, Long subCategoryId, SingleValue<Integer> isActive) throws ServicesException {

        if (!CheckUtil.hasValue(subCategoryId) || subCategoryId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        if (!CheckUtil.hasValue(isActive) || isActive.getValue() < 0 || isActive.getValue() > 1) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        Optional<ServiceItemSubCategory> serviceItemSubCategoryOptional = serviceItemSubCategoryRepository.findById(subCategoryId);
        if (!serviceItemSubCategoryOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        ServiceItemSubCategory serviceItemSubCategory = serviceItemSubCategoryOptional.get();
        if (isActive.getValue() == 0) {
            serviceItemSubCategory.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.INACTIVE.value());
        } else {
            serviceItemSubCategory.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
        }
        serviceItemSubCategoryRepository.save(serviceItemSubCategory);

        return new BaseWrapper(subCategoryId);
    }

    // get list of time slots
    @Override
    public BaseWrapper getListOfTimeSlots(Pageable pageable) throws ServicesException {

        Page<TimeSlots> timeSlotsPage = appointmentTimeSlotsRepository.findAll(pageable);

        if (!timeSlotsPage.hasContent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());

        }

        List<TimeSlots> timeSlots = timeSlotsPage.getContent();
        List<TimeSlotsDTO> timeSlotsList = new ArrayList<>();
        for (TimeSlots appointmentTimeSlots : timeSlots) {
            timeSlotsList.add(new TimeSlotsDTO(appointmentTimeSlots));
        }
        Pagination pagination = new Pagination(timeSlotsList, timeSlotsPage.getTotalElements(), pageable);
        return new BaseWrapper(timeSlotsList, pagination);
    }

    @Override
    public BaseWrapper addTimeSlots(TimeSlotsDTO timeSlot) {
        TimeSlots appointmentTimeSlots = new TimeSlots(timeSlot);
        appointmentTimeSlots.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        appointmentTimeSlotsRepository.save(appointmentTimeSlots);

        return new BaseWrapper(appointmentTimeSlots.getId());
    }

    @Override
    public BaseWrapper updateTimeSlots(TimeSlotsDTO request) throws ServicesException {

        Long timeSlotId = request.getId();
        if (!CheckUtil.hasValue(timeSlotId)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Optional<TimeSlots> timeSlotOptional = appointmentTimeSlotsRepository.findById(timeSlotId);
        if (!timeSlotOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        TimeSlots timeSlot = timeSlotOptional.get();
        timeSlot.setId(request.getId());
        timeSlot.setDescription(request.getDescription());
        timeSlot.setDisplayValue(request.getDisplayValue());
        timeSlot.setStoredValue(request.getStoredValue());
        timeSlot.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
        appointmentTimeSlotsRepository.save(timeSlot);
        return new BaseWrapper(request);
    }

    @Override
    public BaseWrapper deleteTimeSlotById(Long timeSlotId, SingleValue<Integer> isActive) throws ServicesException {
        if (!CheckUtil.hasValue(timeSlotId) || timeSlotId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        if (!CheckUtil.hasValue(isActive) || isActive.getValue() < 0 || isActive.getValue() > 1) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }

        Optional<TimeSlots> timeSlotsOptional = appointmentTimeSlotsRepository.findById(timeSlotId);
        if (!timeSlotsOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        TimeSlots timeSlot = timeSlotsOptional.get();
        if (isActive.getValue() == 0) {
            timeSlot.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.INACTIVE.value());

        } else {
            timeSlot.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        }
        return new BaseWrapper(timeSlotId);
    }

    @Override
    public BaseWrapper getListOfAdvertise(Pageable pageable) throws ServicesException {

        Page<Advertisements> advertisementsPage = advertisementsRepository.findAll(pageable);
        List<AdvertisementDTO> advertisementDTOList = new ArrayList<>();
        if (!advertisementsPage.hasContent()) {
            return new BaseWrapper(advertisementDTOList);
        }

        List<Advertisements> advertisementsList = advertisementsPage.getContent();

        for (Advertisements advertisement : advertisementsList) {
            advertisementDTOList.add(new AdvertisementDTO(advertisement));
        }
        return new BaseWrapper(advertisementDTOList,
                new Pagination(advertisementDTOList, advertisementsPage.getTotalElements(), pageable));
    }


   /* @Override
    public BaseWrapper addNewAdvertise(AdvertisementDTO request) throws ServicesException {
        if (!CheckUtil.hasValue(request)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Advertisements advertisement = new Advertisements(request);
        advertisement.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        advertisementsRepository.save(advertisement);

        return new BaseWrapper(request);
    }*/

    /* @Override
     public BaseWrapper updateAdvertise(AdvertisementDTO request) throws ServicesException {

         Long advertiseId = request.getId();
         if (!CheckUtil.hasValue(advertiseId) || advertiseId <= 0) {
             throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
         }

         Optional<Advertisements> advertisementsOptional = advertisementsRepository.findById(advertiseId);
         if (!advertisementsOptional.isPresent()) {
             throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
         }
         Advertisements advertisement = advertisementsOptional.get();
         advertisement.setId(request.getId());
         advertisement.setSortOrder(request.getSortOrder());
         advertisement.setTitle(request.getTitle());
         advertisement.setType(request.getType());
         *//*advertisement.setUrl(request.getUrl());
        advertisement.setOnClickUrl(request.getOnClickUrl());*//*
        advertisement.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        advertisementsRepository.save(advertisement);

        return new BaseWrapper(request);
    }
*/
    @Override
    public BaseWrapper deleteAdvertise(Long advertiseId, SingleValue<Integer> isActive) throws ServicesException {
        if (!CheckUtil.hasValue(advertiseId) || advertiseId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        if (!CheckUtil.hasValue(isActive) || isActive.getValue() < 0 || isActive.getValue() > 1) {
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
        }
        Optional<Advertisements> advertisementsOptional = advertisementsRepository.findById(advertiseId);
        if (!advertisementsOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        Advertisements advertisement = advertisementsOptional.get();
        if (isActive.getValue() == 0) {
            log.info("authorizationService.fetchLoggedInUser() : {}", authorizationService.fetchLoggedInUser());
            advertisement.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.INACTIVE.value());

        } else {
            advertisement.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        }
        advertisementsRepository.save(advertisement);
        return new BaseWrapper(advertiseId);
    }

    @Override
    public BaseWrapper getAdvertisementById(Long advertiseId) throws ServicesException {

        if (!CheckUtil.hasValue(advertiseId) || advertiseId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Optional<Advertisements> advertisementsOptional = advertisementsRepository.findById(advertiseId);
        if (!advertisementsOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        Advertisements advertisement = advertisementsOptional.get();

        AdvertisementDTO advertisementDTO = new AdvertisementDTO(advertisement);

        return new BaseWrapper(advertisementDTO);
    }

    @Override
    public BaseWrapper getServiceById(Long serviceId) throws ServicesException {
        if (!CheckUtil.hasValue(serviceId) || serviceId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Optional<Services> servicesOptional = servicesRepository.findById(serviceId);
        if (!servicesOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        Services services = servicesOptional.get();
        ServicesDTO servicesDTO = new ServicesDTO(services);
        return new BaseWrapper(servicesDTO);
    }

    @Override
    public BaseWrapper getServiceCategoryById(Long id, Long categoryId) throws ServicesException {
        if (!CheckUtil.hasValue(categoryId) || categoryId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Optional<ServiceItemCategory> servicesCategoryOptional = serviceItemCategoryRepository.findById(categoryId);
        if (!servicesCategoryOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        ServiceItemCategory serviceItemCategory = servicesCategoryOptional.get();
        ServiceItemCategoriesDTO serviceItemCategoriesDTO = new ServiceItemCategoriesDTO(serviceItemCategory);
        return new BaseWrapper(serviceItemCategoriesDTO);
    }

    @Override
    public BaseWrapper getServiceSubCategoryById(Long id, Long categoryId, Long subCategoryId) throws ServicesException {
        if (!CheckUtil.hasValue(subCategoryId) || subCategoryId <= 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Optional<ServiceItemSubCategory> serviceItemSubCategoryOptional = serviceItemSubCategoryRepository.findById(subCategoryId);
        if (!serviceItemSubCategoryOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        ServiceItemSubCategory serviceItemSubCategory = serviceItemSubCategoryOptional.get();
        ServiceItemSubCategoryDTO serviceItemSubCategoryDTO = new ServiceItemSubCategoryDTO(serviceItemSubCategory);

        return new BaseWrapper(serviceItemSubCategoryDTO);
    }

    @Override
    public BaseWrapper fetchCategoryAndSubcategory(Source source, Long id) throws ServicesException {
        if (!CheckUtil.hasValue(id) || id <= 0 || !CheckUtil.hasValue(source)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }

        Optional<Services> servicesOptional = servicesRepository.findById(id);
        if (!servicesOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }

        Services services = servicesOptional.get();
        List<ServiceItemCategoriesDTO> serviceCategories = new ArrayList<>();
        if (source.name().equalsIgnoreCase(Source.APP.name())) {
            List<ServiceItemCategory> serviceItemCategories = serviceItemCategoryRepository.findByServices_IdAndActiveStatus(id, ActiveStatus.ACTIVE.value());
            for (ServiceItemCategory serviceItemCategory : serviceItemCategories) {

                ServiceItemCategoriesDTO category = new ServiceItemCategoriesDTO(serviceItemCategory);

                List<ServiceItemSubCategoryDTO> serviceSubCategories = new ArrayList<>();

                List<ServiceItemSubCategory> serviceItemSubCategories = serviceItemSubCategoryRepository.findByServiceItemCategory_IdAndActiveStatus(serviceItemCategory.getId(), ActiveStatus.ACTIVE.value());
                for (ServiceItemSubCategory serviceItemSubCategory : serviceItemSubCategories) {
                    serviceSubCategories.add(new ServiceItemSubCategoryDTO(serviceItemSubCategory));
                }

                category.setServiceItemSubCategoryList(serviceSubCategories);
                serviceCategories.add(category);
            }
        }
        if (source.name().equalsIgnoreCase(Source.WEB.name())) {

            List<ServiceItemCategory> serviceItemCategories = serviceItemCategoryRepository.findByServices_Id(id);
            for (ServiceItemCategory serviceItemCategory : serviceItemCategories) {

                ServiceItemCategoriesDTO category = new ServiceItemCategoriesDTO(serviceItemCategory);

                List<ServiceItemSubCategoryDTO> serviceSubCategories = new ArrayList<>();

                List<ServiceItemSubCategory> serviceItemSubCategories = serviceItemSubCategoryRepository.findByServiceItemCategory_Id(serviceItemCategory.getId());
                for (ServiceItemSubCategory serviceItemSubCategory : serviceItemSubCategories) {
                    serviceSubCategories.add(new ServiceItemSubCategoryDTO(serviceItemSubCategory));
                }

                category.setServiceItemSubCategoryList(serviceSubCategories);
                serviceCategories.add(category);
            }
        }
        ProServiceListDTO proServiceList = new ProServiceListDTO(services, serviceCategories);


        return new BaseWrapper(proServiceList);
    }

    @Override
    public BaseWrapper getAdvertisementType() {
        List<AdvertisementType> advertisementType = Arrays.asList(AdvertisementType.GENERAL_ADVERTISEMENT);
        return new BaseWrapper(advertisementType);
    }

    @Override
    public BaseWrapper addNewAdvertise(MultipartFile urlFile, MultipartFile onClickUrlFile, Integer sortOrder, String title, AdvertisementType type, Long id, Long urlFileId, Long onClickUrlFileId) throws IOException, ServicesException {

        if (!CheckUtil.hasValue(title) || !CheckUtil.hasValue(type) || !CheckUtil.hasValue(sortOrder)) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Advertisements advertisements = null;

        if (!CheckUtil.hasValue(id) || id == 0) {

            advertisements = new Advertisements(title, type, sortOrder);
            advertisements.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

        } else {
           /* if(id == 0){
                advertisements = new Advertisements(title, type, sortOrder);
                advertisements.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            }*/

            Optional<Advertisements> advertisementsOptional = advertisementsRepository.findById(id);
            if (advertisementsOptional.isPresent()) {

                advertisements = advertisementsOptional.get();
                advertisements.setTitle(title);
                advertisements.setSortOrder(sortOrder);
                advertisements.setType(type);
                advertisements.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());

               /* for (AdvertisementImage image : advertisements.getAdvertisementImageList()) {
                    advertisementImageRepository.delete(image);
                    log.debug("deleted image");
                }*/

            }

        }

        List<AdvertisementImage> advertisementImages = new ArrayList<>();
        FileDetailsDTO fileDetailsDTO = null;
        FileDetailsDTO fileDetails = null;
        // check for urlFile image
        if (CheckUtil.hasValue(urlFile)) {
            fileDetailsDTO = fileUploadService.transferFile(urlFile, fileUploadPath);
            AdvertisementImage image = new AdvertisementImage(fileDetailsDTO, advertisements);
            image.setImageType(AdvertisementImageType.POP_UP_IMAGE);

            if (CheckUtil.hasValue(urlFileId) && urlFileId != 0) {
                image.setId(urlFileId);
                image.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            } else {
                image.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            }

            advertisementImages.add(image);
            log.debug("URL Image added");
        }

        //check for onClickUrlFile Image
        if (CheckUtil.hasValue(onClickUrlFile)) {
            fileDetails = fileUploadService.transferFile(onClickUrlFile, fileUploadPath);
            AdvertisementImage onClickImage = new AdvertisementImage(fileDetails, advertisements);
            onClickImage.setImageType(AdvertisementImageType.CLICK_IMAGE);
            if (CheckUtil.hasValue(onClickUrlFileId) && onClickUrlFileId != 0) {
                onClickImage.setId(onClickUrlFileId);
                onClickImage.updateAuditableFields(false, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            } else {
                onClickImage.updateAuditableFields(true, authorizationService.fetchLoggedInUser().getEmailId(), ActiveStatus.ACTIVE.value());
            }
            advertisementImages.add(onClickImage);
            log.debug("onclickImage added");
        }


        advertisements.setAdvertisementImageList(advertisementImages);

        advertisementsRepository.save(advertisements);
        log.debug("saved advertisements");

        AdvertisementDTO advertisementDTO = new AdvertisementDTO(advertisements);
        return new BaseWrapper(advertisementDTO);
    }

    @Override
    public DownloadImageDTO downloadAdvertisementImage(Long imageId) throws ServicesException {

        if (!CheckUtil.hasValue(imageId) || imageId == 0) {
            throw new ServicesException(GeneralErrorCodes.ERR_REQUIRED_FIELDS_NOT_SUPPLIED.value());
        }
        Optional<AdvertisementImage> advertisementImageOptional = advertisementImageRepository.findById(imageId);
        if (!advertisementImageOptional.isPresent()) {
            throw new ServicesException(GeneralErrorCodes.ERR_RECORD_NOT_FOUND.value());
        }
        AdvertisementImage advertisementImage = advertisementImageOptional.get();

        DownloadImageDTO imageInfo = new DownloadImageDTO();
        imageInfo.setFilePath(advertisementImage.getAdvtImgSaveDirNm() + advertisementImage.getAdvtImgSavedNm());
        imageInfo.setImgExtn(advertisementImage.getAdvtImgExtn());
        imageInfo.setDisplayName(advertisementImage.getAdvtImgDisplayNm());

        return imageInfo;

    }

    @Override
    public BaseWrapper getCurrency(Pageable pageable) {

        Page<Currency> currencyPage = currencyRepository.findAll(pageable);
        List<Currency> currencyList = new ArrayList<>();
        if (currencyPage.hasContent()) {
            currencyList = currencyPage.getContent();
        }
        return new BaseWrapper(currencyList);
    }


}
