package com.prajekpro.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "totalCount",
        "details"
})
public class MetaData<T> {

    @JsonProperty("totalCount")
    private int totalCount;
    @JsonProperty("details")
    private List<T> details = new ArrayList<T>();

    public MetaData(List<T> details) {
        this.totalCount = details.isEmpty() ? 0 : details.size();
        this.details = details;
    }
}
