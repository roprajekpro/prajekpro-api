package com.prajekpro.api.dto.reports;

import com.prajekpro.api.dto.reports.core.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class StackedBarChartDTO extends BaseChartDTO implements Serializable {
    private static final long serialVersionUID = -5121589274365593595L;

    private List<ChartDataSet> chartDataSets = new ArrayList<>();

    @Override
    public void populateData() {
        this.setChartLabels(Arrays.asList("2013", "2014", "2015", "2016", "2017"));
        this.setXAxesTitle("Years");
        this.setYAxesTitle("Currency");
        this.chartDataSets = Arrays.asList(
                new ChartDataSet("Dataset 1", "rgba(255, 99, 132, 1)", Arrays.asList(1l,2l,3l,1l,5l)),
                new ChartDataSet("Dataset 2", "rgba(255, 99, 132, 1)", Arrays.asList(1l,2l,3l,1l,5l)),
                new ChartDataSet("Dataset 3", "rgba(255, 99, 132, 1)", Arrays.asList(1l,2l,3l,1l,5l))
        );
    }
}
