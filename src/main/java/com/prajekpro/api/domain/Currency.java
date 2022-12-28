package com.prajekpro.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name="currency", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
public class Currency {

	@Id
	@Column(length = 3, updatable = false, nullable = false)
    @ApiModelProperty(notes = "ISO Alpha-3 Code for the currency. Ex: INR, USD")
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 1)
    private String symbol;

    public Currency(String currencyId) {
        this.code = currencyId;
    }
}
