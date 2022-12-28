package com.prajekpro.api.dto;

import com.safalyatech.common.dto.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProAppointmentDetailsDTO {

    private List<ProvidedServiceDetailsDTO> providedServiceDetails;
    private AppointmentDetailsMetaDataDTO appointmentDetailsMetaData;

    public ProAppointmentDetailsDTO(List<ProvidedServiceDetailsDTO> providedServiceDetails, AppointmentDetailsMetaDataDTO appointmentDetailsMetaData) {
        this.providedServiceDetails = providedServiceDetails;
        this.appointmentDetailsMetaData = appointmentDetailsMetaData;
    }
}
