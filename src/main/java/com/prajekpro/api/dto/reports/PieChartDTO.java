package com.prajekpro.api.dto.reports;

import com.prajekpro.api.dto.reports.core.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class PieChartDTO extends CommonChartDTO implements Serializable {
    private static final long serialVersionUID = 5275717354869931642L;

    @Override
    public void populateData() {
        this.setChartLabels(Arrays.asList("2013", "2014", "2015", "2016", "2017"));
        this.setXAxesTitle("Years");
        this.setYAxesTitle("Currency");
        this.setToolTipLabel("Test PieChartDTO Data");
        this.setChartData(Arrays.asList(2l,7l,12l,14l,17l));
        this.setBackgroundColor(Arrays.asList(
                "rgba(255, 99, 132, 1)",
                "rgba(54, 162, 235, 1)",
                "rgba(255, 206, 86, 1)",
                "rgba(75, 192, 192, 1)",
                "rgba(255, 159, 64, 1)"
        ));
    }
}
