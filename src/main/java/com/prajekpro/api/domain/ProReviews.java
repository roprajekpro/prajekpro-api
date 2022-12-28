package com.prajekpro.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.prajekpro.api.dto.ProReviewsDTO;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.Users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "pro_reviews")
public class ProReviews extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "FK_CUSTOMER_ID")
	private Users customer;

	@ManyToOne
	@JoinColumn(name = "FK_PRO_ID")
	private ProDetails proDetails;

	@Column(name = "REVIEW")
	private String review;

	@Column(name = "STAR_RATING")
	private Float starRating;

	@Column(name = "FK_APPOINTMENT_ID")
	private Long appointmentId;

}
