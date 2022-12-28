package com.prajekpro.api.dto;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "additionalData",
        "pspReference",
        "resultCode",
        "amount",
        "merchantReference"
})
@Generated("jsonschema2pojo")
public class PaymentDetailsResponseDTO {

    @JsonProperty("additionalData")
    private PaymentDetailsAdditionalData additionalData;
    @JsonProperty("pspReference")
    private String pspReference;
    @JsonProperty("resultCode")
    private String resultCode;
    @JsonProperty("amount")
    private PaymentDetailsAmount amount;
    @JsonProperty("merchantReference")
    private String merchantReference;

    /**
     * No args constructor for use in serialization
     *
     */
    public PaymentDetailsResponseDTO() {
    }

    /**
     *
     * @param amount
     * @param resultCode
     * @param additionalData
     * @param pspReference
     * @param merchantReference
     */
    public PaymentDetailsResponseDTO(PaymentDetailsAdditionalData additionalData, String pspReference, String resultCode, PaymentDetailsAmount amount, String merchantReference) {
        super();
        this.additionalData = additionalData;
        this.pspReference = pspReference;
        this.resultCode = resultCode;
        this.amount = amount;
        this.merchantReference = merchantReference;
    }

    @JsonProperty("additionalData")
    public PaymentDetailsAdditionalData getAdditionalData() {
        return additionalData;
    }

    @JsonProperty("additionalData")
    public void setAdditionalData(PaymentDetailsAdditionalData additionalData) {
        this.additionalData = additionalData;
    }

    @JsonProperty("pspReference")
    public String getPspReference() {
        return pspReference;
    }

    @JsonProperty("pspReference")
    public void setPspReference(String pspReference) {
        this.pspReference = pspReference;
    }

    @JsonProperty("resultCode")
    public String getResultCode() {
        return resultCode;
    }

    @JsonProperty("resultCode")
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @JsonProperty("amount")
    public PaymentDetailsAmount getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(PaymentDetailsAmount amount) {
        this.amount = amount;
    }

    @JsonProperty("merchantReference")
    public String getMerchantReference() {
        return merchantReference;
    }

    @JsonProperty("merchantReference")
    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(PaymentDetailsResponseDTO.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("additionalData");
        sb.append('=');
        sb.append(((this.additionalData == null)?"<null>":this.additionalData));
        sb.append(',');
        sb.append("pspReference");
        sb.append('=');
        sb.append(((this.pspReference == null)?"<null>":this.pspReference));
        sb.append(',');
        sb.append("resultCode");
        sb.append('=');
        sb.append(((this.resultCode == null)?"<null>":this.resultCode));
        sb.append(',');
        sb.append("amount");
        sb.append('=');
        sb.append(((this.amount == null)?"<null>":this.amount));
        sb.append(',');
        sb.append("merchantReference");
        sb.append('=');
        sb.append(((this.merchantReference == null)?"<null>":this.merchantReference));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }



}
