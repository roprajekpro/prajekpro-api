package com.prajekpro.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FilterParamDTO {
    private String rating;
    private String date;
    private String price;
}
