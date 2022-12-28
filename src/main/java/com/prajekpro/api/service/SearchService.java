package com.prajekpro.api.service;

import com.prajekpro.api.dto.MetaData;
import com.prajekpro.api.dto.ProServiceItemsPricingDTO;
import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;

public interface SearchService {
    BaseWrapper searchServices(String term) throws ServicesException;
}
