package com.prajekpro.api.service;

import com.prajekpro.api.dto.LocationDetailsDTO;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;

public interface LocationService {

    BaseWrapper updateUserLocation(LocationDetailsDTO request) throws ServicesException;

    BaseWrapper updateUserLocation(Long proId, LocationDetailsDTO request) throws ServicesException;

    BaseWrapper getUserLocationDetails();

    BaseWrapper getUserLocationDetails(Long proId);

    BaseWrapper getLookUpList(String reference, String term) throws Exception;
}
