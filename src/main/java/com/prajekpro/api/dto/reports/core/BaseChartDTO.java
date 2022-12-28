package com.prajekpro.api.dto.reports.core;

import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public abstract class BaseChartDTO implements Serializable {
    private static final long serialVersionUID = -262031411290777683L;

    private List<String> chartLabels = new ArrayList<>();
    private String xAxesTitle;
    private String yAxesTitle;

    public abstract void populateData();
}
