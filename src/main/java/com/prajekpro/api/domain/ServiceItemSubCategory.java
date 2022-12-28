package com.prajekpro.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.safalyatech.common.domains.Auditable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "service_item_subcategory")
public class ServiceItemSubCategory extends Auditable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@NotBlank(message = "Service Subcategory name is Mandatory")
	@Column(name = "SERVICE_ITEM_SUB_CATEGORY_NAME", nullable = false, length = 50)
	private String itemSubCategoryName;
	
	@Column(name = "SERVICE_ITEM_SUB_CATEGORY_DESC", nullable = true, length = 500)
	private String itemSubCategoryDesc;
	
	@Column(name = "PARENT_ID", nullable = false, length = 4)
	private Long parentId = 0l;

	@NotNull(message = "fillipino price is mandatory")
	@PositiveOrZero(message = "fillipino price should be positive")
	@Column(name = "DEFAULT_FILLIPINO_PRICE", nullable = false, length = 5)
	private Float defaultFillipinoPrice;
	
	@ManyToOne
	@JoinColumn(name = "SERVICE_ITEM_CATEGORY_ID")
	private ServiceItemCategory serviceItemCategory;

	@ManyToOne
	@JoinColumn(name = "FK_SUBSCRIPTION_CURRENCY_ID")
	private Currency currency;

    public ServiceItemSubCategory(Long serviceSubCategoryId) {
    	this.id=serviceSubCategoryId;
    }
}
