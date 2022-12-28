package com.prajekpro.api.service;

import com.adyen.service.exception.ApiException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.exception.*;
import com.prajekpro.api.repository.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public interface AppointmentService {

    BaseWrapper getAppointmentTimeSlots(AppointmentSlotsDTO request, String timeZone) throws ServicesException, JsonParseException, JsonMappingException, IOException;

    BaseWrapper bookAppointment(AppointmentBookingDTO request) throws ServicesException, ParseException;

    BaseWrapper getListOfAppointment(String timeZone, Pageable pageable, AppointmentState status);

    BaseWrapper getAppointmentDetails(Long id) throws ServicesException;

    BaseWrapper updateAppointmentState(Long appointmentId, AppointmentState state, RemarksDTO cancellationRemarks,
                                       Long appointmentRequestedServiceId, Users loggedInUser) throws ServicesException, ParseException;

    BaseWrapper reScheduleAppointment(AppointmentServicesDTO appointmentServices, Long appointmentId) throws ServicesException, ParseException;

    BaseWrapper getProAppointmentList(Long serviceId, String term, String status, String timeZone, Pageable pageable, FilterParamDTO filterRequest) throws ServicesException, PPServicesException;

    BaseWrapper uploadCustomerSign(Long appointmentId, MultipartFile file) throws ServicesException;

    ResponseEntity<ByteArrayResource> downloadCustomerSign(Long appointmentId) throws ServicesException, IOException;

    BaseWrapper generateInvoice(Long appointmentId, GenerateInvoiceWrapperDTO appointmentDetails) throws ServicesException;

    BaseWrapper getProAppointmentDetailsList(Long serviceId, String timeZone, String term, String status, Pageable pageable) throws ServicesException, PPServicesException;

    /*BaseWrapper getParticularToAddServices() throws ServicesException;*/

    BaseWrapper storeUserAppointmentAddress(UserDeliveryAddressDTO userAddress);

    BaseWrapper getUserAppointmentAddress() throws ServicesException;

    BaseWrapper deleteUserAppointmentAddress(Long addressId) throws ServicesException;

    BaseWrapper getProjectOverview();

    BaseWrapper getProReview(Pageable pageable);

    BaseWrapper appointmentSearch(AppointmentSearchRequestBodyDTO request, Pageable pageable) throws ServicesException;

    BaseWrapper getAppointmentList(AppointmentSearchRequestBodyDTO request, Pageable pageable);

    BaseWrapper sendInvoice(Long appointmentId) throws ServicesException, ParseException;

    BaseWrapper initiateAppointmentPayment(Long appointmentId) throws ServicesException, IOException, ApiException;

    void updateAppointmentTables(Integer txdId, PaymentDetails paymentDetails, PPPaymentResponseType ppPaymentResponseFromResultCode);

    BaseWrapper initiateAppointmentCancellation(Long appointmentId) throws ServicesException, IOException, ApiException, ParseException;

    void updateAppointmentCancellationTables(Integer txdId, PaymentDetails paymentDetails, PPPaymentResponseType ppPaymentResponseFromResultCode) throws ServicesException, ParseException;

    InvoiceDtl selectAppointmentInvoiceDtl(Long apptId) throws ServicesException;

    BaseWrapper updateProApptDocs(Long apptId, DocType docType, MultipartFile file) throws ServicesException, IOException;

    DownloadImageDTO getApptDocs(String id) throws ServicesException;

    List<AppointmentDetailsRepository.ProJobsCompleted> getJobsCompletedForProIdIn(List<Long> prosWithStartingCostIds) throws ServicesException;
}
