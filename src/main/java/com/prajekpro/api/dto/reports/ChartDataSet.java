package com.prajekpro.api.dto.reports;

import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class ChartDataSet implements Serializable {
    private static final long serialVersionUID = -2100081990264066993L;

    private String label;
    private List<Long> data = new ArrayList<>();
    private String backgroundColor;

    public ChartDataSet(String label, String backgroundColor, List<Long> data) {
        this.label = label;
        this.backgroundColor = backgroundColor;
        this.data = data;
    }
}
