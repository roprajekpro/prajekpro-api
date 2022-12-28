package com.prajekpro.api.service;

import com.prajekpro.api.domain.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.enums.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.exception.ServicesException;
import org.springframework.web.multipart.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

public interface ProServicesService {

    BaseWrapper storeProSchedule(ProServiceScheduleDTO request, Long serviceId) throws ServicesException;

    BaseWrapper getProServiceSchedule(Long proId) throws ServicesException;

    void updateProServices(Long proId, List<Services> request) throws ServicesException;

    BaseWrapper getProServices(Long proId) throws ServicesException;

    BaseWrapper getProSubCategoryPricing(Long serviceId) throws ServicesException;

    BaseWrapper storeProSubCategoryPrice(Long serviceId, ProServiceListDTO request) throws ServicesException;

    BaseWrapper deleteSubCategoryPrice(Long serviceId, List<Long> requestId) throws ServicesException;

    BaseWrapper updateProCancellationTime(ProCancellationTimeDTO request, HttpServletRequest servletRequest) throws ServicesException;

    BaseWrapper deleteProServices(Long proId, Long serviceId) throws ServicesException;

    BaseWrapper uploadProServiceDocument(Long proId, Long serviceId, DocType docType, MultipartFile file) throws ServicesException, IOException;

    BaseWrapper updateProService(Long proId, Long serviceId, ProServiceUpdateDTO request) throws ServicesException;

    DownloadImageDTO getProServiceDocDetails(String id) throws ServicesException;

    BaseWrapper updateProComment(Long proId, Long serviceId, ProServiceCommentsDTO request) throws ServicesException;

    BaseWrapper getProServiceComments(Long proId, Long serviceId) throws ServicesException;

    BaseWrapper getProServiceUnavailableDates(Long proId, Long serviceId) throws ServicesException;

    BaseWrapper addProServiceUnavailableDate(Long proId, Long serviceId, ProServiceUnavailableDates request) throws ServicesException;

    BaseWrapper deleteProServiceUnavailableDate(Long proId, Long serviceId, Long unavailableDateId) throws ServicesException;

    BaseWrapper deleteUploadProServiceDocument(Long proId, Long serviceId, Long docId) throws ServicesException;
}
