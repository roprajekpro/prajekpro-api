package com.prajekpro.api.domain;

import com.prajekpro.api.enums.ValueType;
import com.safalyatech.common.domains.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "tax_config")
public class TaxConfig extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LABEL")
    private String label;

    @Column(name = "LABEL_DESC")
    private String labelDesc;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "VALUE_TYPE")
    private ValueType valueType;

    @ManyToOne
    @JoinColumn(name = "FK_CURRENCY_CODE")
    private Currency currency;

    public TaxConfig(Long id) {
        this.id = id;
    }
}
