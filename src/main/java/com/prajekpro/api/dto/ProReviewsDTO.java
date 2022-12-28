package com.prajekpro.api.dto;

import com.prajekpro.api.domain.ProReviews;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class ProReviewsDTO {

    private Long id;
    private String proName;
    private Long proId;
    private String customerName;
    private String userId;
    private String review;
    private Float starRating;
    private Long appointmentId;

    public ProReviewsDTO(ProReviews proReviews) {

        this.id = proReviews.getId();
        this.proName = proReviews.getProDetails().getUserDetails().getFullName();
        this.customerName = proReviews.getCustomer().getFullName();
        this.review = proReviews.getReview();
        this.starRating = proReviews.getStarRating();
    }
}
