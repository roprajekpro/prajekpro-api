package com.prajekpro.api.dto;

import com.safalyatech.common.enums.*;
import lombok.*;

import java.io.*;

@Setter
@Getter
@NoArgsConstructor
public class ProServiceUpdateDTO implements Serializable {

    private ActiveStatus activeStatus;
    private boolean prajekProVerified;
    private boolean certified;
    private ProServiceCommentsDTO comment;
}
