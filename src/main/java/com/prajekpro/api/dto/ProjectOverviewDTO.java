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
public class ProjectOverviewDTO {

    private Long totalCompletedProjects;
    private List<ProvidedServiceDetailsDTO> providedServiceDetailsList;
    private Double averageRating;
    private int countOfRatingsRecord;


    public ProjectOverviewDTO(Long totalAppointments, List<ProvidedServiceDetailsDTO> providedServiceDetails, Double averageRating, int ratingRecordsSize) {
        this.totalCompletedProjects = totalAppointments;
        this.providedServiceDetailsList = providedServiceDetails;
        this.averageRating = averageRating;
        this.countOfRatingsRecord = ratingRecordsSize;
    }

    public ProjectOverviewDTO(Long totalAppointments, Double averageRating, int ratingRecordsSize) {
        this.totalCompletedProjects = totalAppointments;
        this.averageRating = averageRating;
        this.countOfRatingsRecord = ratingRecordsSize;
    }
}
