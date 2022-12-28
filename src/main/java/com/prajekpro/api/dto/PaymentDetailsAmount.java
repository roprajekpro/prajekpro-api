package com.prajekpro.api.dto;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "currency",
        "value"
})
@Generated("jsonschema2pojo")
public class PaymentDetailsAmount {


    @JsonProperty("currency")
    private String currency;
    @JsonProperty("value")
    private Long value;

    /**
     * No args constructor for use in serialization
     *
     */
    public PaymentDetailsAmount() {
    }

    /**
     *
     * @param currency
     * @param value
     */
    public PaymentDetailsAmount(String currency, Long value) {
        super();
        this.currency = currency;
        this.value = value;
    }

    @JsonProperty("currency")
    public String getCurrency() {
        return currency;
    }

    @JsonProperty("currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("value")
    public Long getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PaymentDetailsAmount.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("currency");
        sb.append('=');
        sb.append(((this.currency == null)?"<null>":this.currency));
        sb.append(',');
        sb.append("value");
        sb.append('=');
        sb.append(((this.value == null)?"<null>":this.value));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }



}
