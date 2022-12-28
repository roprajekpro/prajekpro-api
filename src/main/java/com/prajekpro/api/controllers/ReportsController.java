package com.prajekpro.api.controllers;

import com.prajekpro.api.constants.*;
import com.prajekpro.api.dto.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.exception.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {
        RestUrlConstants.PP_REPORTS
})
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @PostMapping()
    public BaseWrapper getReports(
            @RequestBody ReportRequestDTO request
    ) throws ServicesException {
        return reportsService.getReports(request);
    }
}
