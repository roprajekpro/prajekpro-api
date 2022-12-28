package com.prajekpro.api.dto;

import com.prajekpro.api.domain.AppointmentTaxDetails;
import com.prajekpro.api.domain.Currency;
import com.prajekpro.api.domain.TaxConfig;
import com.prajekpro.api.enums.ValueType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class TaxConfigDTO {

    private Long id;

    private Long appointmentTaxId;

    private String label;

    private String labelDesc;

    private String value;

    private ValueType valueType;

    private Currency currency;

    private Float taxAmount;

    public TaxConfigDTO(TaxConfig taxConfig) {
        this.id = taxConfig.getId();
        this.label = taxConfig.getLabel();
        this.labelDesc = taxConfig.getLabelDesc();
        this.value = taxConfig.getValue();
        this.valueType = taxConfig.getValueType();
        this.currency = taxConfig.getCurrency();
    }

    public TaxConfigDTO(AppointmentTaxDetails taxDetails) {
        this.id = taxDetails.getTaxConfig().getId();
        this.appointmentTaxId = taxDetails.getId();
        this.currency = taxDetails.getCurrency();
        this.label = taxDetails.getTaxConfig().getLabel();
        this.labelDesc = taxDetails.getTaxConfig().getLabelDesc();
        this.value = taxDetails.getTaxValue();
        this.valueType=taxDetails.getTaxValueType();
        this.taxAmount = Float.valueOf(taxDetails.getTaxAmount());
    }
}
