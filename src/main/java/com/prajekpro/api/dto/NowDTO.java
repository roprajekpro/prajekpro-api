package com.prajekpro.api.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
public class NowDTO {

    private Long currentEpochTime;

    public NowDTO(long currentEpochTime) {
        this.currentEpochTime = currentEpochTime;
    }
}
