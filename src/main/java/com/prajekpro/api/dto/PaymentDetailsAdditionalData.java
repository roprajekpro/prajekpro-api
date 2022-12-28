package com.prajekpro.api.dto;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "requestAmount",
        "requestCurrencyCode",
        "requestAcquirerReference"
})
@Generated("jsonschema2pojo")
public class PaymentDetailsAdditionalData {

    @JsonProperty("requestAmount")
    private String requestAmount;
    @JsonProperty("requestCurrencyCode")
    private String requestCurrencyCode;
    @JsonProperty("requestAcquirerReference")
    private String requestAcquirerReference;

    /**
     * No args constructor for use in serialization
     */
    public PaymentDetailsAdditionalData() {
    }

    /**
     * @param requestAcquirerReference
     * @param requestAmount
     * @param requestCurrencyCode
     */
    public PaymentDetailsAdditionalData(String requestAmount, String requestCurrencyCode, String requestAcquirerReference) {
        super();
        this.requestAmount = requestAmount;
        this.requestCurrencyCode = requestCurrencyCode;
        this.requestAcquirerReference = requestAcquirerReference;
    }

    @JsonProperty("requestAmount")
    public String getRequestAmount() {
        return requestAmount;
    }

    @JsonProperty("requestAmount")
    public void setRequestAmount(String requestAmount) {
        this.requestAmount = requestAmount;
    }

    @JsonProperty("requestCurrencyCode")
    public String getRequestCurrencyCode() {
        return requestCurrencyCode;
    }

    @JsonProperty("requestCurrencyCode")
    public void setRequestCurrencyCode(String requestCurrencyCode) {
        this.requestCurrencyCode = requestCurrencyCode;
    }

    @JsonProperty("requestAcquirerReference")
    public String getRequestAcquirerReference() {
        return requestAcquirerReference;
    }

    @JsonProperty("requestAcquirerReference")
    public void setRequestAcquirerReference(String requestAcquirerReference) {
        this.requestAcquirerReference = requestAcquirerReference;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PaymentDetailsAdditionalData.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("requestAmount");
        sb.append('=');
        sb.append(((this.requestAmount == null) ? "<null>" : this.requestAmount));
        sb.append(',');
        sb.append("requestCurrencyCode");
        sb.append('=');
        sb.append(((this.requestCurrencyCode == null) ? "<null>" : this.requestCurrencyCode));
        sb.append(',');
        sb.append("requestAcquirerReference");
        sb.append('=');
        sb.append(((this.requestAcquirerReference == null) ? "<null>" : this.requestAcquirerReference));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}

