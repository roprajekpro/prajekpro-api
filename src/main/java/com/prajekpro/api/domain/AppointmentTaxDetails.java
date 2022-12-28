package com.prajekpro.api.domain;

import com.prajekpro.api.dto.TaxConfigDTO;
import com.prajekpro.api.enums.ValueType;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.utility.CheckUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "appointment_tax_details")
public class AppointmentTaxDetails extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_APPOINTMENT_ID")
    private AppointmentDetails appointmentDetails;

    @ManyToOne
    @JoinColumn(name = "FK_TAX_CONFIG")
    private TaxConfig taxConfig;

    @Column(name = "TAX_VALUE")
    private String taxValue;

    @Column(name = "TAX_VALUE_TYPE")
    private ValueType taxValueType;

    @ManyToOne
    @JoinColumn(name = "FK_CURRENCY_ID")
    private Currency currency;

    @Column(name = "TAX_AMOUNT")
    private String taxAmount;

    public AppointmentTaxDetails(TaxConfigDTO request, AppointmentDetails appointmentDetails) {
        if(CheckUtil.hasValue(request.getAppointmentTaxId()) && request.getAppointmentTaxId()>0){
            this.id = request.getAppointmentTaxId();
        }
        this.appointmentDetails = appointmentDetails;
        this.taxAmount = String.valueOf(request.getTaxAmount());
        this.currency = request.getCurrency();
        this.taxValueType = request.getValueType();
        this.taxValue = request.getValue();
        TaxConfig taxConfig = new TaxConfig(request.getId());
        this.taxConfig = taxConfig;
    }
}
