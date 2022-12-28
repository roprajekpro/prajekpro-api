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
@Table(name = "pro_service_items_pricing")
public class ProServiceItemsPricing extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "FK_PRO_SERVICE_ITEMS_PRICING_PRO_ID")
	private ProDetails proDetails;
	
	@ManyToOne
	@JoinColumn(name = "FK_PRO_SERVICE_ITEMS_PRICING_SERVICE_ID")
	private Services services;
	
	@ManyToOne
	@JoinColumn(name = "FK_PRO_SERVICE_ITEMS_PRICING_SERVICE_ITEM_SUBCATEGORY_ID")
	private ServiceItemSubCategory serviceItemSubcategory;

	@Column(name="price")
	private Float price;
	
	@ManyToOne
	@JoinColumn(name = "FK_PRO_SERVICE_ITEMS_PRICING_CURRENCY_ID")
	private Currency currency;
}
