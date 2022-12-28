package com.prajekpro.api.dto;

import com.prajekpro.api.enums.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ReportRequestDTO implements Serializable {
    private static final long serialVersionUID = 159862476911504750L;

    private ReportDuration reportDuration;
    private Set<ReportType> reportType;
    private Set<GraphType> graphType;
}
