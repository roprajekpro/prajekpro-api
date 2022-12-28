package com.prajekpro.api.service;

import com.safalyatech.common.dto.BaseWrapper;
import com.safalyatech.common.exception.ServicesException;

public interface CommonService {

    BaseWrapper getUsers();

    BaseWrapper getDashboardInfo() throws ServicesException;
}
