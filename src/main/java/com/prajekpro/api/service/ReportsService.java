package com.prajekpro.api.service;

import com.prajekpro.api.dto.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.exception.*;

public interface ReportsService {
    BaseWrapper getReports(ReportRequestDTO request) throws ServicesException;
}
