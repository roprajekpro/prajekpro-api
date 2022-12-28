package com.prajekpro.api.dto;

import com.prajekpro.api.domain.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ProServiceUnavailableDatesDTO implements Serializable {

    private static final long serialVersionUID = 8336832502871773144L;
    private Long id;
    private String startDt;
    private String endDt;

    public ProServiceUnavailableDatesDTO(ProServiceUnavailableDates input) {
        this.id = input.getId();
        this.startDt = input.getStartDt();
        this.endDt = input.getEndDt();
    }
}
