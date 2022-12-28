package com.prajekpro.api.service;

import com.prajekpro.api.dto.ServiceItemCategoriesDTO;
import com.prajekpro.api.dto.ServiceItemSubCategoryDTO;
import com.prajekpro.api.dto.ServicesDTO;
import com.prajekpro.api.dto.TimeSlotsDTO;
import com.prajekpro.api.enums.AdvertisementType;
import com.prajekpro.api.enums.Source;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.dto.DownloadImageDTO;
import com.safalyatech.common.dto.SingleValue;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface MasterService {
    BaseWrapper addServices(ServicesDTO request) throws ServicesException;

    BaseWrapper getListOfServices(Source source, Pageable pageable) throws ServicesException;

    BaseWrapper editServices(ServicesDTO request) throws ServicesException;

    BaseWrapper deleteService(Long id, SingleValue<Integer> isActive) throws ServicesException;

    BaseWrapper getListOfServiceCategories(Source source, Long id, Pageable pageable) throws ServicesException;

    BaseWrapper addServiceCategory(ServiceItemCategoriesDTO request, Long id);

    BaseWrapper updateServiceCategory(ServiceItemCategoriesDTO request, Long id) throws ServicesException;

    BaseWrapper deleteServiceCategory(Long id, Long categoryId, SingleValue<Integer> isActive) throws ServicesException;

    BaseWrapper getListOfServiceSubcategory(Source source, Long id, Long categoryId, Pageable pageable) throws ServicesException;

    BaseWrapper addServiceSubCategory(ServiceItemSubCategoryDTO request, Long id, Long categoryId) throws ServicesException;

    BaseWrapper updateServiceSubCategory(ServiceItemSubCategoryDTO request, Long id, Long categoryId) throws ServicesException;

    BaseWrapper deleteServiceSubCategory(Long id, Long categoryId, Long subCategoryId, SingleValue<Integer> isActive) throws ServicesException;

    BaseWrapper getListOfTimeSlots(Pageable pageable) throws ServicesException;

    BaseWrapper addTimeSlots(TimeSlotsDTO timeSlot);

    BaseWrapper updateTimeSlots(TimeSlotsDTO timeSlot) throws ServicesException;

    BaseWrapper deleteTimeSlotById(Long timeSlotId, SingleValue<Integer> isActive) throws ServicesException;

    BaseWrapper getListOfAdvertise(Pageable pageable) throws ServicesException;

    /*BaseWrapper addNewAdvertise(AdvertisementDTO request) throws ServicesException;*/

/*
    BaseWrapper updateAdvertise(AdvertisementDTO request) throws ServicesException;
*/

    BaseWrapper deleteAdvertise(Long advertiseId, SingleValue<Integer> isActive) throws ServicesException;

    BaseWrapper getAdvertisementById(Long advertiseId) throws ServicesException;

    BaseWrapper getServiceById(Long serviceId) throws ServicesException;

    BaseWrapper getServiceCategoryById(Long id, Long categoryId) throws ServicesException;

    BaseWrapper getServiceSubCategoryById(Long id, Long categoryId, Long subCategoryId) throws ServicesException;

    BaseWrapper fetchCategoryAndSubcategory(Source source, Long id) throws ServicesException;

    BaseWrapper getAdvertisementType();

    BaseWrapper addNewAdvertise(MultipartFile urlFile, MultipartFile onClickUrlFile, Integer sortOrder, String title, AdvertisementType type, Long id, Long urlFileId, Long onClickUrlFileId) throws IOException, ServicesException;

    DownloadImageDTO downloadAdvertisementImage(Long imageId) throws ServicesException;

    BaseWrapper getCurrency(Pageable pageable);
}
