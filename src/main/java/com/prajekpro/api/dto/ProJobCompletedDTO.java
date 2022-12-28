package com.prajekpro.api.dto;

import lombok.*;

import java.io.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class ProJobCompletedDTO implements Serializable {
    private static final long serialVersionUID = 3589868765521732043L;

    private long proId;
    private int jobsCompleted;
}
