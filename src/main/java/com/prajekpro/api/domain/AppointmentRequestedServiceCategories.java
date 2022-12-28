package com.prajekpro.api.domain;

import com.prajekpro.api.dto.AppointmentServiceCategoryDTO;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.utility.CheckUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor

@Table(name = "appointment_requested_service_categories")
public class AppointmentRequestedServiceCategories extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MASTER_ID")
    private AppointmentRequestedServices appointmentRequestedServices;

    @ManyToOne
    @JoinColumn(name = "SERVICE_CATEGORY_ID")
    private ServiceItemCategory serviceItemCategory;

    @OneToMany(targetEntity = AppointmentRequestedServiceSubCategories.class,mappedBy = "appointmentRequestedServiceCategories",cascade = CascadeType.ALL)
    private List<AppointmentRequestedServiceSubCategories> appointmentRequestedServiceSubCategories;

    public AppointmentRequestedServiceCategories(AppointmentServiceCategoryDTO category, AppointmentRequestedServices service) {
       if(CheckUtil.hasValue(category.getAppointmentRequestedServiceCategoryId()) && category.getAppointmentRequestedServiceCategoryId() > 0){
           this.id = category.getAppointmentRequestedServiceCategoryId();
       }
        this.serviceItemCategory = new ServiceItemCategory(category.getServiceCategoryId());
        this.appointmentRequestedServices = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentRequestedServiceCategories that = (AppointmentRequestedServiceCategories) o;
        return Objects.equals(id, that.id) && Objects.equals(serviceItemCategory, that.serviceItemCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serviceItemCategory);
    }
}
