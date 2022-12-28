package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.*;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.exception.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = {
        RestUrlConstants.PP_PRO_SERVICES
})
@Api(value = "API related to appointment management")
public class ProServiceController {

    @Autowired
    private ProServicesService proServicesService;
    @Autowired
    private ProService proService;

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

    @ApiOperation(value = "Api to get vendor services")
    @GetMapping(value = {
            RestUrlConstants.PP_REGISTER_PRO_SERVICE
    })
    public BaseWrapper getVendorServices(
            @PathVariable(value = "pro-id") Long proId) throws ServicesException {

        return proServicesService.getProServices(proId);
    }

    @ApiOperation(value = "Api to delete vendor services")
    @DeleteMapping(value = {
            RestUrlConstants.PP_REGISTER_DELETE_PRO_SERVICES
    })
    public BaseWrapper deleteVendorServices(
            @PathVariable(value = "pro-id") Long proId, @PathVariable(value = "serviceId") Long serviceId) throws ServicesException {

        return proServicesService.deleteProServices(proId, serviceId);
    }

    @ApiOperation(value = "API to Store Pro Schedule for Services")
    @PutMapping(value = {RestUrlConstants.PP_PRO_SERVICE_SCHEDULE})
    public BaseWrapper saveProScheduleForService(@RequestBody ProServiceScheduleDTO request, @PathVariable("serviceId") Long serviceId) throws ServicesException {
        return proServicesService.storeProSchedule(request, serviceId);
    }

    @ApiOperation(value = "API to get pro schedule for service")
    @GetMapping(value = {RestUrlConstants.PP_PRO_SERVICE_SCHEDULE})
    public BaseWrapper getProServiceSchedule(@PathVariable("serviceId") Long serviceId) throws ServicesException {
        return proServicesService.getProServiceSchedule(serviceId);
    }

    // api to get pro subcategory price
    @ApiOperation(value = "API to get pro services with Subcategory pricing")
    @GetMapping(value = {RestUrlConstants.PP_PRO_SERVICE_SUBCATEGORY_PRICING})
    public BaseWrapper getProServiceSubcategoryPricing(@PathVariable("serviceId") Long serviceId) throws ServicesException {
        return proServicesService.getProSubCategoryPricing(serviceId);
    }

    // api to save subcategory price
    @ApiOperation(value = "api to save service subcategory pricing")
    @PostMapping(value = {RestUrlConstants.PP_PRO_SERVICE_SUBCATEGORY_PRICING})
    public BaseWrapper storeSubcategoryPricingForPro(@PathVariable("serviceId") Long serviceId, @RequestBody ProServiceListDTO request) throws ServicesException {
        return proServicesService.storeProSubCategoryPrice(serviceId, request);
    }

    //api to delete subcategory price
    @ApiOperation(value = "api to delete service subcategory pricing")
    @DeleteMapping(value = {RestUrlConstants.PP_PRO_SERVICE_SUBCATEGORY_PRICING})
    public BaseWrapper deleteSubcategoryPricingForPro(@PathVariable("serviceId") Long serviceId, @RequestBody List<Long> requestId) throws ServicesException {
        return proServicesService.deleteSubCategoryPrice(serviceId, requestId);
    }

    //API to Add Cancellation time for Pro and service
    @ApiOperation(value = "API to Add Cancellation time for Pro")
    @PostMapping(value = {RestUrlConstants.PP_PRO_CANCELLATION_TIME})
    public BaseWrapper updateProCancellationTime(
            @RequestBody ProCancellationTimeDTO request,
            HttpServletRequest servletRequest) throws ServicesException {
        return proServicesService.updateProCancellationTime(request, servletRequest);
    }

    @PutMapping(RestUrlConstants.PP_PRO_SERVICE_UPDATE)
    @ApiOperation(value = "API to update Pro Service")
    public BaseWrapper updateProService(
            @PathVariable(value = "pro-id") Long proId,
            @PathVariable(value = "serviceId") Long serviceId,
            @RequestBody ProServiceUpdateDTO request
    ) throws ServicesException {
        return proServicesService.updateProService(proId, serviceId, request);
    }

    @PutMapping(value = RestUrlConstants.PP_PRO_SERVICE_COMMENT)
    @ApiOperation(value = "Api to update comments for a particular service provided by a PRO")
    public BaseWrapper updateProServiceComment(
            @PathVariable(value = GlobalConstants.StringConstants.PRO_ID) Long proId,
            @PathVariable(value = GlobalConstants.StringConstants.SERVICE_ID) Long serviceId,
            @RequestBody ProServiceCommentsDTO request
    ) throws ServicesException {
        return proServicesService.updateProComment(proId, serviceId, request);
    }

    @GetMapping(value = RestUrlConstants.PP_PRO_SERVICE_COMMENT)
    @ApiOperation(value = "Api to get comments for a particular service provided by a PRO")
    public BaseWrapper getProServiceComment(
            @PathVariable(value = GlobalConstants.StringConstants.PRO_ID) Long proId,
            @PathVariable(value = GlobalConstants.StringConstants.SERVICE_ID) Long serviceId
    ) throws ServicesException {
        return proServicesService.getProServiceComments(proId, serviceId);
    }

    @GetMapping(value = RestUrlConstants.PP_PRO_SERVICE_UNAVAILABLE_DATES)
    @ApiOperation(value = "Api to get unavailable dates for a particular service provided by a PRO")
    public BaseWrapper getProServiceUnavailableDates(
            @PathVariable(value = GlobalConstants.StringConstants.PRO_ID) Long proId,
            @PathVariable(value = GlobalConstants.StringConstants.SERVICE_ID) Long serviceId
    ) throws ServicesException {
        return proServicesService.getProServiceUnavailableDates(proId, serviceId);
    }

    @PostMapping(value = RestUrlConstants.PP_PRO_SERVICE_UNAVAILABLE_DATES)
    @ApiOperation(value = "Api to add an unavailable date for a particular service provided by a PRO")
    public BaseWrapper addProServiceUnavailableDate(
            @PathVariable(value = GlobalConstants.StringConstants.PRO_ID) Long proId,
            @PathVariable(value = GlobalConstants.StringConstants.SERVICE_ID) Long serviceId,
            @RequestBody ProServiceUnavailableDates request
    ) throws ServicesException {
        return proServicesService.addProServiceUnavailableDate(proId, serviceId, request);
    }

    @DeleteMapping(value = RestUrlConstants.PP_PRO_SERVICE_UNAVAILABLE_DATES_DELETE)
    @ApiOperation(value = "Api to delete an unavailable date for a particular service provided by a PRO")
    public BaseWrapper deleteProServiceUnavailableDate(
            @PathVariable(value = GlobalConstants.StringConstants.PRO_ID) Long proId,
            @PathVariable(value = GlobalConstants.StringConstants.SERVICE_ID) Long serviceId,
            @PathVariable(value = "unavailableDateId") Long unavailableDateId
    ) throws ServicesException {
        return proServicesService.deleteProServiceUnavailableDate(proId, serviceId, unavailableDateId);
    }
}
