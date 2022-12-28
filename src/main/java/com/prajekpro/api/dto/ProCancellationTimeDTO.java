package com.prajekpro.api.dto;


import com.prajekpro.api.domain.ProCancellationTime;
import com.prajekpro.api.domain.Services;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProCancellationTimeDTO {
    private Long proCancellationTimeId;
    private Long proId;
    private Long serviceId;
    private Long cancellationTime;
    private String cancellationTimeUnit;
    private Long cancellationTimeUnitId;
    private Long cancellationFees;
    private String cancellationFeesUnit;
    private Long cancellationFeesUnitId;

    public ProCancellationTimeDTO(ProCancellationTime proCancellationTime) {
        this.proCancellationTimeId = proCancellationTime.getId();
        this.proId = proCancellationTime.getProDetails().getId();
        this.serviceId = proCancellationTime.getServices().getId();
        this.cancellationTime = proCancellationTime.getCancellationTime();
        this.cancellationTimeUnit = proCancellationTime.getCancellationTimeUnit().getValue();
        log.debug("Cancellation time Unit Id = {}", proCancellationTime.getCancellationTimeUnit().getId());
        this.cancellationTimeUnitId = proCancellationTime.getCancellationTimeUnit().getId();

    }

    public ProCancellationTimeDTO(Services services) {
        this.cancellationTime = services.getCancellationTime();
        this.cancellationTimeUnit = services.getCancellationTimeUnit().getValue();
        this.cancellationTimeUnitId = services.getCancellationTimeUnit().getId();
    }
}
