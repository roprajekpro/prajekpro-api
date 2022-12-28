package com.prajekpro.api.service.impl;

import com.prajekpro.api.dto.*;
import com.prajekpro.api.dto.reports.*;
import com.prajekpro.api.dto.reports.core.*;
import com.prajekpro.api.enums.*;
import com.prajekpro.api.service.*;
import com.safalyatech.common.dto.*;
import com.safalyatech.common.exception.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Service
@Transactional
public class ReportsServiceImpl implements ReportsService {

    @Override
    public BaseWrapper getReports(ReportRequestDTO request) throws ServicesException {
        if (!hasValue(request.getReportType())
                || !hasValue(request.getReportDuration())
                || !hasValue(request.getGraphType()))
            throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());

        Map<String, BaseChartDTO> reportsData = new HashMap<>();

        for (GraphType graphType : request.getGraphType()) {
            BaseChartDTO baseChartDTO = null;
            switch (graphType) {
                case BAR:
                    baseChartDTO = new BarChartDTO();
                    baseChartDTO.populateData();
                    break;

                case DONUT:
                    baseChartDTO = new DonutChartDTO();
                    baseChartDTO.populateData();
                    break;

                case PIE:
                    baseChartDTO = new PieChartDTO();
                    baseChartDTO.populateData();
                    break;

                case STACKED_BAR:
                    baseChartDTO = new StackedBarChartDTO();
                    baseChartDTO.populateData();
                    break;

                default:
                    throw new ServicesException(GeneralErrorCodes.ERR_INVALID_DATA_SUPPLIED.value());
            }

            reportsData.put(graphType.name(), baseChartDTO);
        }

        return new BaseWrapper(reportsData);
    }
}
