package com.prajekpro.api.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class GenerateInvoiceWrapperDTO {
    private AppointmentServicesDTO appointmentServicesDTO;
    private List<AppointmentOtherServicesDTO> appointmentOtherServices;
    private Double subTotal = 0d;
    private Double grandTotal = 0d;
    private List<TaxConfigDTO> applicableTaxes = new ArrayList<>();
}
