package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.RestUrlConstants;
import com.prajekpro.api.dto.ProReviewsDTO;
import com.prajekpro.api.dto.ProSearchRequestBodyDTO;
import com.prajekpro.api.dto.StatusRemarkDTO;
import com.safalyatech.common.dto.SingleValue;
import com.prajekpro.api.enums.AvailabilityStatus;
import com.prajekpro.api.service.ProService;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.dto.SingleValue;
import com.safalyatech.common.enums.ActiveStatus;
import com.safalyatech.common.exception.ServicesException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(
        value = {
                RestUrlConstants.PP_PRO
        })
@Api(value = "PRO Api Calls across the application for PP")
public class ProController {

    @Autowired
    private ProService proService;

    @ApiOperation(value = "api to get Pro List for Admin panel")
    @PostMapping(value = {RestUrlConstants.PP_PRO_LIST})
    public BaseWrapper getProList(@RequestBody ProSearchRequestBodyDTO request, Pageable pageable) {
        return proService.getProList(request, pageable);
    }

    @ApiOperation(value = "api to get pro details (basic and overview details)")
    @GetMapping(value = {RestUrlConstants.PP_ADMIN_PRO_DETAILS})
    public BaseWrapper getProDetails(@PathVariable("proId") Long proId) throws ServicesException {
        return proService.getProDetails(proId);
    }

    @ApiOperation(value = "APi for pro Appointment List ")
    @GetMapping(value = {RestUrlConstants.PP_ADMIN_PRO_APPOINTMENTS})
    public BaseWrapper getProAppointments(@PathVariable("proId") Long proId, Pageable pageable) throws ServicesException {
        return proService.getProAppointments(proId, pageable);
    }

    @ApiOperation(value = "get Vendor reviews list ")
    @GetMapping(value = {RestUrlConstants.PP_ADMIN_PRO_REVIEWS})
    public BaseWrapper getProReviews(@PathVariable("proId") Long proId, Pageable pageable) {
        return proService.getProReview(proId, pageable);
    }

    @ApiOperation(value = "APi for pro Appointment History ")
    @GetMapping(value = {RestUrlConstants.PP_ADMIN_PRO_APPOINTMENTS_HISTORY})
    public BaseWrapper getProAppointmentsHistory(@PathVariable("proId") Long proId, Pageable pageable) throws ServicesException {
        return proService.getProAppointmentsHistory(proId, pageable);
    }

    @ApiOperation(value = "API to Active/DeActivate pro")
    @PutMapping(value = {RestUrlConstants.PP_ADMIN_PRO_DEACTIVATE})
    public BaseWrapper getProDeactivated(@RequestBody StatusRemarkDTO statusRemark, @PathVariable("proId") Long proId) throws ServicesException {
        return proService.getProDeactivated(proId, statusRemark);
    }

    @ApiOperation(value = "API to store pro-reviews ")
    @PostMapping(value = {RestUrlConstants.PP_STORE_REVIEWS})
    public BaseWrapper storeReviews(@RequestBody ProReviewsDTO request) throws ServicesException {
        return proService.storeReviews(request);
    }

    @ApiOperation(value = "API to Set Availability Status of pro")
    @PutMapping(value = {RestUrlConstants.PP_PRO_AVAILABILITY})
    public BaseWrapper updateAvailabilityStatus(@PathVariable("status") AvailabilityStatus status) {
        return proService.updateProAvailabilityStatus(status);
    }
}
