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
        "displayValue",
        "icon",
        "type"
})
public class Detail {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("displayValue")
    private String displayValue;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("type")
    private String type;

    public Detail(Long id, String displayValue, String icon) {
        this.id = id;
        this.displayValue = displayValue;
        this.icon = icon;
    }


}
