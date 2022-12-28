package com.prajekpro.api.dto;

import com.prajekpro.api.domain.Services;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ServicesDTO {

    private Long id;
    private Integer sortOrder;
    private String serviceName;
    private String serviceIcon;
    private String description;
    private Long cancellationTime;
    private Long cancellationFees;
    private String cancellationTimeUnit;
    private String cancellationFeesUnit;
    private Long cancellationTimeUnitId;
    private Long cancellationFeesUnitId;
    private Integer isActive;


    public ServicesDTO(Services service) {
        this.id = service.getId();
        this.serviceIcon = service.getServiceIcon();
        this.serviceName = service.getServiceName();
        this.sortOrder = service.getSortOrder();
        this.description = service.getDescription();
        this.cancellationTime = service.getCancellationTime();
        this.cancellationFees = service.getCancellationFees();
        this.cancellationTimeUnit = service.getCancellationTimeUnit().getValue();
        this.cancellationFeesUnit = service.getCancellationFeesUnit().getValue();
        this.cancellationTimeUnitId = service.getCancellationTimeUnit().getId();
        this.cancellationFeesUnitId = service.getCancellationFeesUnit().getId();
        this.isActive = service.getActiveStatus();
    }
}
