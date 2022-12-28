
package com.prajekpro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "proImageUrl",
        "proName",
        "proOccupation",
        "appointmentTime",
        "status"
})
public class UpcomingAppointmentVO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("proImageUrl")
    private String proImageUrl;
    @JsonProperty("proName")
    private String proName;
    @JsonProperty("proOccupation")
    private String proOccupation;
    @JsonProperty("appointmentTime")
    private String appointmentTime;
    @JsonProperty("status")
    private String status;

}
