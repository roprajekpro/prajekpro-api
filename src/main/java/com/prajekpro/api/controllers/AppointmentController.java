package com.prajekpro.api.controllers;

import com.adyen.service.exception.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.prajekpro.api.constants.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.exception.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.constants.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.exception.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.io.*;
import java.text.*;

@Slf4j
@RestController
@RequestMapping(value = {
        RestUrlConstants.PP_APPOINTMENT
})
@Api(value = "API related to appointment management")
public class AppointmentController {


    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AuthorizationService authorizationService;


    @ApiOperation(value = "Get time slots defined by a Vendor for a date")
    @PostMapping(value = {RestUrlConstants.PP_APPOINTMENT_TIME_SLOTS})
    public BaseWrapper getAppointmentTimeSlots(
            @RequestBody AppointmentSlotsDTO request,
            @RequestHeader(value = GlobalConstants.HEADER_TIMEZONE, required = false, defaultValue = GlobalConstants.DEFAULT_TIMEZONE_ID) String timeZone) throws ServicesException, JsonParseException, JsonMappingException, IOException {
        return appointmentService.getAppointmentTimeSlots(request, timeZone);
    }

    @ApiOperation(value = "api for booking appointment")
    @PostMapping()
    public BaseWrapper bookAppointment(@RequestBody AppointmentBookingDTO request) throws ServicesException, ParseException {
        return appointmentService.bookAppointment(request);
    }

    @ApiOperation(value = "API for display Appointment List")
    @GetMapping()
    public BaseWrapper getListOfAppointment(
            @RequestParam(value = "status", required = false, defaultValue = GlobalConstants.AppointmentStatus.DEFAULT) AppointmentState status,
            @RequestHeader(value = GlobalConstants.HEADER_TIMEZONE, required = false, defaultValue = GlobalConstants.DEFAULT_TIMEZONE_ID) String timeZone, Pageable pageable) {
        return appointmentService.getListOfAppointment(timeZone, pageable, status);
    }

    @ApiOperation(value = "API for Appointment Details")
    @GetMapping(value = {RestUrlConstants.PP_BOOKED_APPOINTMENT_DETAILS})
    public BaseWrapper getBookedAppointmentDetails(@PathVariable("id") Long id) throws ServicesException {
        return appointmentService.getAppointmentDetails(id);
    }

    @ApiOperation(value = "API for update Appointment State")
    @PutMapping(value = {RestUrlConstants.PP_UPDATE_APPOINTMENT_STATE})
    public BaseWrapper updateAppointmentState(
            @PathVariable("appointmentId") Long appointmentId,
            @PathVariable("state") AppointmentState state,
            @RequestBody RemarksDTO cancellationRemarks,
            @PathVariable("appointmentRequestedServiceId") Long appointmentRequestedServiceId) throws ServicesException, ParseException {

        //Fetch Logged In User Details
        Users loggedInUser = authorizationService.fetchLoggedInUser();

        return appointmentService.updateAppointmentState(appointmentId, state, cancellationRemarks, appointmentRequestedServiceId, loggedInUser);
    }

    @ApiOperation(value = "API for re-schedule Appointment")
    @PutMapping(value = {RestUrlConstants.PP_RE_SCHEDULE_APPOINTMENT})
    public BaseWrapper reScheduleAppointment(@RequestBody AppointmentServicesDTO appointmentServices, @PathVariable("appointmentId") Long appointmentId) throws ServicesException, ParseException {
        return appointmentService.reScheduleAppointment(appointmentServices, appointmentId);
    }

    @ApiOperation(value = "API for Appointment List at pro Side")
    @GetMapping(value = {RestUrlConstants.PP_PRO_APPOINTMENT_LIST})
    public BaseWrapper getProAppointmentList(
            @PathVariable("serviceId") Long serviceId,
            @RequestParam(value = GlobalConstants.StringConstants.TERM, required = false) String term,
            @RequestParam(value = GlobalConstants.StringConstants.STATUS, required = false, defaultValue = GlobalConstants.AppointmentStatus.DEFAULT) String status,
            @RequestHeader(value = GlobalConstants.HEADER_TIMEZONE, required = false, defaultValue = GlobalConstants.DEFAULT_TIMEZONE_ID) String timeZone,
            Pageable pageable) throws ServicesException, PPServicesException {
        FilterParamDTO filterRequest = null;
        return appointmentService.getProAppointmentList(serviceId, term, status, timeZone, pageable, filterRequest);
    }

    @ApiOperation(value = "API For Appointment Details list (current and past) at pro side")
    @GetMapping(value = {RestUrlConstants.PP_PRO_APPOINTMENT_DETAILS_LIST})
    public BaseWrapper getProAppointmentDetailsList(
            @PathVariable("serviceId") Long serviceId,
            @RequestParam(value = GlobalConstants.StringConstants.TERM, required = false) String term,
            @RequestParam(value = GlobalConstants.StringConstants.STATUS, required = false, defaultValue = GlobalConstants.AppointmentStatus.DEFAULT) String status,
            @RequestHeader(value = GlobalConstants.HEADER_TIMEZONE, required = false, defaultValue = GlobalConstants.DEFAULT_TIMEZONE_ID) String timeZone,
            Pageable pageable) throws ServicesException, PPServicesException {
        return appointmentService.getProAppointmentDetailsList(serviceId, timeZone, term, status, pageable);
    }
/*

	@ApiOperation(value = "API for add particulars at pro side ")
	@GetMapping(value = {RestUrlConstants.PP_APPOINTMENT_ADD_PARTICULAR})
	public BaseWrapper getServicesForParticulars() throws ServicesException {
		return appointmentService.getParticularToAddServices();
	}
*/

    @ApiOperation(value = "API to upload customer signature")
    @PostMapping(value = RestUrlConstants.PP_UPLOAD_CUSTOMER_SIGN)
    public BaseWrapper uploadCustomerSign(
            @PathVariable("appointmentId") Long appointmentId,
            @RequestParam("file") MultipartFile file) throws ServicesException {
        return appointmentService.uploadCustomerSign(appointmentId, file);
    }

    @ApiOperation(value = "Api to get signature for a customer")
    @GetMapping(value = RestUrlConstants.PP_DOWNLOAD_CUSTOMER_SIGN)
    public ResponseEntity<ByteArrayResource> downloadCustomerSign(
            @PathVariable("appointmentId") Long appointmentId) throws ServicesException, IOException {

        return appointmentService.downloadCustomerSign(appointmentId);
    }

    @ApiOperation(value = "Api to Generate Invoice")
    @PutMapping(value = {RestUrlConstants.PP_APPOINTMENT_INVOICE})
    public BaseWrapper generateInvoice(@PathVariable("appointmentId") Long appointmentId, @RequestBody GenerateInvoiceWrapperDTO request) throws ServicesException {
        return appointmentService.generateInvoice(appointmentId, request);
    }

    @ApiOperation(value = "API to Store User Delivery Address")
    @PostMapping(value = {RestUrlConstants.PP_APPOINTMENT_ADDRESS})
    public BaseWrapper storeUserAppointmentAddress(@RequestBody UserDeliveryAddressDTO userAddress) {
        return appointmentService.storeUserAppointmentAddress(userAddress);
    }

    @ApiOperation(value = "API to get User Delivery Address")
    @GetMapping(value = {RestUrlConstants.PP_APPOINTMENT_ADDRESS})
    public BaseWrapper getUserAppointmentAddress() throws ServicesException {
        return appointmentService.getUserAppointmentAddress();
    }

    @ApiOperation(value = "API to Delete User Delivery Address")
    @DeleteMapping(value = {RestUrlConstants.PP_APPOINTMENT_DELETE_ADDRESS})
    public BaseWrapper deleteUserAppointmentAddress(@PathVariable("addressId") Long addressId) throws ServicesException {
        return appointmentService.deleteUserAppointmentAddress(addressId);
    }

    @ApiOperation(value = " get overview about projects in vendor app")
    @GetMapping(value = {RestUrlConstants.PP_PROJECTS_OVERVIEW})
    public BaseWrapper getProjectOverviewForVendor() {
        return appointmentService.getProjectOverview();
    }

    @ApiOperation(value = "get reviews list for Overviews")
    @GetMapping(value = {RestUrlConstants.PP_PRO_REVIEWS})
    public BaseWrapper getProReviews(Pageable pageable) {
        return appointmentService.getProReview(pageable);
    }

    @ApiOperation(value = "APi to search appointments")
    @PostMapping(value = {RestUrlConstants.PP_APPOINTMENT_SEARCH})
    public BaseWrapper appointmentSearch(@RequestBody AppointmentSearchRequestBodyDTO request,
                                         Pageable pageable) throws ServicesException {

        log.info("Request Parameters : [{}]", request.toString());
        return appointmentService.appointmentSearch(request, pageable);
    }

    @ApiOperation(value = " API to Send Invoice")
    @PostMapping(value = {RestUrlConstants.PP_SEND_INVOICE})
    public BaseWrapper sendInvoice(@PathVariable("appointmentId") Long appointmentId) throws ServicesException, ParseException {
        return appointmentService.sendInvoice(appointmentId);
    }

    @ApiOperation(value = "API to initiate payment for appointment")
    @GetMapping(value = {RestUrlConstants.PP_APPOINTMENT_PAYMENT})
    public BaseWrapper initiateAppointmentPayment(@PathVariable("appointmentId") Long appointmentId) throws ServicesException, IOException, ApiException {
        return appointmentService.initiateAppointmentPayment(appointmentId);
    }

    @ApiOperation(value = "API to initiate payment cancellation")
    @GetMapping(value = {RestUrlConstants.PP_CANCEL_APPOINTMENT})
    public BaseWrapper initiateAppointmentCancellation(@PathVariable("appointmentId") Long appointmentId) throws ServicesException, IOException, ApiException, ParseException {
        return appointmentService.initiateAppointmentCancellation(appointmentId);
    }

    //API's for admin panel

    @ApiOperation(value = "API for display Appointment List for ADMIN panel")
    @PostMapping(value = {RestUrlConstants.PP_ADMIN_APPOINTMENTS})
    public BaseWrapper getAppointmentList(@RequestBody AppointmentSearchRequestBodyDTO request, Pageable pageable) {
        return appointmentService.getAppointmentList(request, pageable);
    }

    @PutMapping("/{appt-id}/documents")
    public BaseWrapper updateProApptDocs(
            @PathVariable(value = "appt-id") Long apptId,
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "type") DocType docType
    ) throws ServicesException, IOException {
        return appointmentService.updateProApptDocs(apptId, docType, file);
    }

}
