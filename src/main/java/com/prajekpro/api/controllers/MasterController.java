package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.dto.ServiceItemCategoriesDTO;
import com.prajekpro.api.dto.ServiceItemSubCategoryDTO;
import com.prajekpro.api.dto.ServicesDTO;
import com.prajekpro.api.dto.TimeSlotsDTO;
import com.prajekpro.api.enums.AdvertisementType;
import com.prajekpro.api.enums.Source;
import com.prajekpro.api.service.MasterService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.dto.SingleValue;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;


@RestController
@Api(value = "Master Service Api Calls across the application for PP")
@RequestMapping(value = {RestUrlConstants.PP_MASTER})
public class MasterController {

    @Autowired
    private MasterService masterService;

    @ApiOperation(value = "Api to display list of services")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_SERVICES})
    public BaseWrapper listServices(@PathVariable("source") Source source, Pageable pageable) throws ServicesException {
        return masterService.getListOfServices(source, pageable);
    }

    @ApiOperation(value = "Api to save new service")
    @PostMapping(value = {RestUrlConstants.PP_MASTER_SERVICES})
    public BaseWrapper addNewService(@ApiParam(value = "new service data") @RequestBody ServicesDTO request) throws ServicesException {
        return masterService.addServices(request);
    }

    @ApiOperation(value = "Api to update service")
    @PutMapping(value = {RestUrlConstants.PP_MASTER_SERVICES})
    public BaseWrapper updateServices(@ApiParam(value = "service data for update ") @RequestBody ServicesDTO request) throws ServicesException {
        return masterService.editServices(request);
    }

    @ApiOperation(value = "Api to Active/DeActive service")
    @PutMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_BY_ID})
    public BaseWrapper deleteServices(@PathVariable("id") Long id, @RequestBody SingleValue<Integer> isActive) throws ServicesException {
        return masterService.deleteService(id, isActive);
    }

    @ApiOperation(value = "Api to display list of service categories")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_CATEGORY})
    public BaseWrapper getServiceCategories(@PathVariable("source") Source source, @PathVariable("id") Long id, Pageable pageable) throws ServicesException {
        return masterService.getListOfServiceCategories(source, id, pageable);
    }


    @ApiOperation(value = "api to add new service category")
    @PostMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_CATEGORY})
    public BaseWrapper saveServiceCategory(@ApiParam(value = "value to save new service category") @RequestBody ServiceItemCategoriesDTO request, @PathVariable("id") Long id) {
        return masterService.addServiceCategory(request, id);
    }

    @ApiOperation(value = "api to update service category")
    @PutMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_CATEGORY})
    public BaseWrapper updateServiceCategory(@ApiParam(value = "value to update service category") @RequestBody ServiceItemCategoriesDTO request, @PathVariable("id") Long id) throws ServicesException {
        return masterService.updateServiceCategory(request, id);
    }

    @ApiOperation(value = "api to Active/DeActive service category")
    @PutMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_CATEGORY_BY_ID})
    public BaseWrapper deleteServiceCategory(@RequestBody SingleValue<Integer> isActive, @PathVariable("id") Long id, @PathVariable("categoryId") Long categoryId) throws ServicesException {
        return masterService.deleteServiceCategory(id, categoryId, isActive);
    }

    @ApiOperation(value = "api to display list of services subcategories")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_SUBCATEGORY})
    public BaseWrapper listOfServiceSubcategories(@PathVariable("source") Source source, Pageable pageable, @PathVariable("id") Long id, @PathVariable("categoryId") Long categoryId) throws ServicesException {
        return masterService.getListOfServiceSubcategory(source, id, categoryId, pageable);
    }

    @ApiOperation(value = "api to add new service subcategory")
    @PostMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_SUBCATEGORY})
    public BaseWrapper addServiceSubcategory(@Valid @RequestBody ServiceItemSubCategoryDTO request, @PathVariable("id") Long id, @PathVariable("categoryId") Long categoryId) throws ServicesException {
        return masterService.addServiceSubCategory(request, id, categoryId);
    }

    @ApiOperation(value = "api to update service subcategory")
    @PutMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_SUBCATEGORY})
    public BaseWrapper updateServiceSubCategory(@RequestBody ServiceItemSubCategoryDTO request, @PathVariable("id") Long id, @PathVariable("categoryId") Long categoryId) throws ServicesException {
        return masterService.updateServiceSubCategory(request, id, categoryId);
    }

    @ApiOperation(value = "api to Active/DeActive service subcategory")
    @PutMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_SUBCATEGORY_BY_ID})
    public BaseWrapper deleteServiceSubCategory(@RequestBody SingleValue<Integer> isActive, @PathVariable("id") Long id, @PathVariable("categoryId") Long categoryId, @PathVariable("subCategoryId") Long subCategoryId) throws ServicesException {
        return masterService.deleteServiceSubCategory(id, categoryId, subCategoryId, isActive);
    }

    @ApiOperation(value = "api to list time slots ")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_TIMESLOTS})
    public BaseWrapper getTimeSlotList(Pageable pageable) throws ServicesException {
        return masterService.getListOfTimeSlots(pageable);
    }

    @ApiOperation(value = "api to Add time slots")
    @PostMapping(value = {RestUrlConstants.PP_MASTER_TIMESLOTS})
    public BaseWrapper addTimeSlots(@RequestBody TimeSlotsDTO timeSlot) {
        return masterService.addTimeSlots(timeSlot);
    }

    @ApiOperation(value = "api to update timeSlots")
    @PutMapping(value = {RestUrlConstants.PP_MASTER_TIMESLOTS})
    public BaseWrapper updateTimeSlots(@RequestBody TimeSlotsDTO timeSlot) throws ServicesException {
        return masterService.updateTimeSlots(timeSlot);
    }

    @ApiOperation(value = "api to Active/DeActive timeSlot")
    @PutMapping(value = {RestUrlConstants.PP_MASTER_TIMESLOT_BY_ID})
    public BaseWrapper deleteTimeSlot(@RequestBody SingleValue<Integer> isActive, @PathVariable("timeSlotId") Long timeSlotId) throws ServicesException {
        return masterService.deleteTimeSlotById(timeSlotId, isActive);
    }

    @ApiOperation(value = "api to get list of Advertise")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_ADVERTISE})
    public BaseWrapper getListOfAdvertise(Pageable pageable) throws ServicesException {
        return masterService.getListOfAdvertise(pageable);
    }

   /* @ApiOperation(value = "api to add new advertise")
    @PostMapping(value = {RestUrlConstants.PP_MASTER_ADVERTISE})
    public BaseWrapper addNewAdvertise(@RequestBody AdvertisementDTO request) throws ServicesException {
        return masterService.addNewAdvertise(request);
    }*/

    /* @ApiOperation(value = "Api to download Advertisement Image")
     @GetMapping(value = RestUrlConstants.PP_DOWNLOAD_ADVERTISEMENT_IMAGE)
     public ResponseEntity<ByteArrayResource> downloadAdvertisementImage(
             @PathVariable("imageId") Long imageId) throws ServicesException {
         return masterService.downloadAdvertisementImage(imageId);
     }*/
    @ApiOperation(value = "api to add new advertise or update Advertise")
    @PostMapping(value = {RestUrlConstants.PP_MASTER_ADVERTISE})
    public BaseWrapper addNewAdvertise(@RequestParam(value = "urlFile", required = false) MultipartFile urlFile, @RequestParam(value = "onClickUrlFile", required = false) MultipartFile onClickUrlFile, @RequestParam("sortOrder") Integer sortOrder,
                                       @RequestParam("title") String title, @RequestParam("type") AdvertisementType type, @RequestParam(value = "id", required = false) Long id,
                                       @RequestParam(value = "urlFileId", required = false) Long urlFileId, @RequestParam(value = "onClickUrlFileId", required = false) Long onClickUrlFileId) throws ServicesException, IOException {
        return masterService.addNewAdvertise(urlFile, onClickUrlFile, sortOrder, title, type, id, urlFileId, onClickUrlFileId);
    }

    @ApiOperation(value = "api to activate/deactivate advertise")
    @PutMapping(value = {RestUrlConstants.PP_MASTER_ADVERTISE_BY_ID})
    public BaseWrapper deleteAdvertise(@RequestBody SingleValue<Integer> isActive, @PathVariable("advertiseId") Long advertiseId) throws ServicesException {
        return masterService.deleteAdvertise(advertiseId, isActive);
    }

    @ApiOperation(value = "api to get Advertisement By Id")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_ADVERTISE_BY_ID})
    public BaseWrapper getAdvertiseById(@PathVariable("advertiseId") Long advertiseId) throws ServicesException {
        return masterService.getAdvertisementById(advertiseId);
    }

    @ApiOperation(value = "api to get Service By Id")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_BY_ID})
    public BaseWrapper getServicesById(@PathVariable("id") Long ServiceId) throws ServicesException {
        return masterService.getServiceById(ServiceId);
    }

    @ApiOperation(value = "api to get Service Category By Id")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_CATEGORY_BY_ID})
    public BaseWrapper getServiceCategoryById(@PathVariable("id") Long id, @PathVariable("categoryId") Long categoryId) throws ServicesException {
        return masterService.getServiceCategoryById(id, categoryId);
    }

    @ApiOperation(value = "api to get Service SubCategory By Id")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_SERVICES_SUBCATEGORY_BY_ID})
    public BaseWrapper getServiceSubCategoryById(@PathVariable("id") Long id, @PathVariable("categoryId") Long categoryId, @PathVariable("subCategoryId") Long subCategoryId) throws ServicesException {
        return masterService.getServiceSubCategoryById(id, categoryId, subCategoryId);
    }

    // get category and subcategory by service id
    @ApiOperation(value = "Api to get list of category n subcategory by Service Id")
    @GetMapping(value = {RestUrlConstants.PP_MASTER_CATEGORY_SUBCATEGORY_BY_SERVICE_ID})
    public BaseWrapper getCategoryAndSubCategory(@PathVariable("source") Source source, @PathVariable("id") Long id) throws ServicesException {
        return masterService.fetchCategoryAndSubcategory(source, id);
    }

    @ApiOperation(value = "API to get Advertisement Type")
    @GetMapping(value = {RestUrlConstants.PP_ADVERTISEMENT_TYPE})
    public BaseWrapper getAdvertisementType() {
        return masterService.getAdvertisementType();
    }

    @ApiOperation(value = "API to get Currency ")
    @GetMapping(value = {RestUrlConstants.PP_CURRENCY})
    public BaseWrapper getCurrency(Pageable pageable) {
        return masterService.getCurrency(pageable);
    }
}
