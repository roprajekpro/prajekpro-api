package com.prajekpro.api.dto;


import com.prajekpro.api.domain.PaymentDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaymentDTO {

    private Long paymentId;
    private String paymentMethod;
    private String paymentStatus;


    public PaymentDTO(PaymentDetails paymentDetails) {
        this.paymentId = paymentDetails.getId();
        this.paymentMethod = paymentDetails.getPaymentMethod();
        this.paymentStatus = paymentDetails.getPaymentStatus();
    }
}
