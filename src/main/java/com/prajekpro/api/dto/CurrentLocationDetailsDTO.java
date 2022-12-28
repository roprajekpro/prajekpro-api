package com.prajekpro.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "value",
        "latitude",
        "longitude"
})
public class CurrentLocationDetailsDTO {

    @JsonProperty("value")
    private String value;
    @JsonProperty("latitude")
    private Float latitude;
    @JsonProperty("longitude")
    private Float longitude;
}
