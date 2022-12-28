package com.prajekpro.api.service;

import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.ProReviewsDTO;
import com.prajekpro.api.dto.ProSearchRequestBodyDTO;
import com.prajekpro.api.dto.StatusRemarkDTO;
import com.prajekpro.api.enums.AvailabilityStatus;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.data.domain.Pageable;

import java.util.*;


public interface ProService {
    BaseWrapper getProList(ProSearchRequestBodyDTO request, Pageable pageable);

    BaseWrapper getProDetails(Long proId) throws ServicesException;

    List<ProvidedServiceDetailsDTO> getProServices(Long proId, long totalAppointments);

    BaseWrapper getProAppointments(Long proId, Pageable pageable) throws ServicesException;

    BaseWrapper getProReview(Long proId, Pageable pageable);

    BaseWrapper getProAppointmentsHistory(Long proId, Pageable pageable) throws ServicesException;

    BaseWrapper getProDeactivated(Long proId, StatusRemarkDTO isAdminDeactivated) throws ServicesException;

    BaseWrapper storeReviews(ProReviewsDTO request) throws ServicesException;

    BaseWrapper updateProAvailabilityStatus(AvailabilityStatus status);
}
