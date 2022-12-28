package com.prajekpro.api.dto.reports.core;

import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public abstract class CommonChartDTO extends BaseChartDTO implements Serializable {
    private static final long serialVersionUID = 6963668741315652061L;

    private String toolTipLabel;
    private List<Long> chartData = new ArrayList<>();
    private List<String> backgroundColor = new ArrayList<>();
}
