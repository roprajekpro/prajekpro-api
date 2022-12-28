package com.prajekpro.api.domain;

import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.PPLookUp;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "pro_details")
public class ProDetails extends Auditable implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "FK_PRO_DETAILS_USER_ID")
	private Users userDetails;

	private Long ratings;
	private Long ratedBy;

	private float experienceInYears;
	private int totalJobsCompleted;
	private int availabilityStatus;
	private String aboutText;
	@Column(name = "IS_VAT_REGISTERED")
	private boolean vatRegistered;
	private String vatNo;

	@Transient
	private Long startingCost;
	@Transient
	private String startingCostCurrency;

	@Column(name = "APPLICATION_NO")
	private int applicationNo;
	
	@ManyToMany(targetEntity = Services.class)
    @JoinTable(name = "pro_services",
            joinColumns = {@JoinColumn(name = "FK_PRO_SERVICES_PRO_ID")},
            inverseJoinColumns = {@JoinColumn(name = "fk_PRO_SERVICES_SERVICE_ID")}
    )
	private Set<Services> proServices = new HashSet<>();


/*	@ManyToMany(targetEntity = PPLookUp.class)
	@JoinTable(name = "pro_available_day",
			joinColumns = {@JoinColumn(name = "FK_PRO_ID")},
			inverseJoinColumns = {@JoinColumn(name = "FK_AVAILABLE_DAY")}
	)
	private Set<PPLookUp> availableDays = new HashSet<>();*/
	
//	@OneToMany(mappedBy = "proDetails", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<ProServicesLocations> serviceLocations;
	
	@OneToMany(mappedBy = "proDetails", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProServiceItemsPricing> proServiceItemsPricing;
	
	@OneToMany(mappedBy = "proDetails", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProDocuments> documents;
	
	@OneToMany(mappedBy = "proDetails", cascade = CascadeType.ALL, orphanRemoval = false)
	private List<ProReviews> reviews;

	public ProDetails(Long proId) {
		this.id=proId;
	}

	@OneToMany(mappedBy = "proDetails",targetEntity = ProSubscription.class)
	private List<ProSubscription> proSubscriptions;
}
