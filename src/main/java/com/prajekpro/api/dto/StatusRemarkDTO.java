package com.prajekpro.api.dto;

import com.safalyatech.common.enums.ActiveStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StatusRemarkDTO {
    private ActiveStatus activeStatus;
    private String statusRemark;
}
