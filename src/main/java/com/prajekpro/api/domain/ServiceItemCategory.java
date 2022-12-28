package com.prajekpro.api.domain;

import com.safalyatech.common.domains.CommonMasterTablesData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_item_category")
public class ServiceItemCategory extends CommonMasterTablesData<Long> {

    @ManyToOne
    @JoinColumn(name = "SERVICE_ID")
    private Services services;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "serviceItemCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceItemSubCategory> serviceItemSubCategory;

    public ServiceItemCategory(Long id) {
        super(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceItemCategory)) return false;
        ServiceItemCategory that = (ServiceItemCategory) o;
        return id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
