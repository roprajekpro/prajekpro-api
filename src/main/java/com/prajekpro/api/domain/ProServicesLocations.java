package com.prajekpro.api.domain;

import javax.persistence.*;

import com.safalyatech.common.domains.Auditable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "pro_services_locations")
public class ProServicesLocations extends Auditable {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "FK_PRO_SERVICES_LOCATIONS_PRO_ID")
	private ProDetails proDetails;

	@ManyToOne
	@JoinColumn(name = "FK_SERVICES_SERVICE_ID")
	private Services serviceDetails;

	@Column(name = "latitude")
	private double latitude;

	@Column(name = "longitude")
	private double longitude;
}
