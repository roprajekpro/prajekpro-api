package com.prajekpro.api.domain;

import com.prajekpro.api.dto.*;
import com.safalyatech.common.domains.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.codehaus.jackson.annotate.*;

import javax.persistence.*;
import java.util.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Slf4j
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString

@Table(name = "appointment_requested_service_sub_categories")
public class AppointmentRequestedServiceSubCategories extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MASTER_ID")
    private AppointmentRequestedServiceCategories appointmentRequestedServiceCategories;

    @ManyToOne
    @JoinColumn(name = "SERVICE_SUB_CATEGORY_ID")
    private ServiceItemSubCategory serviceItemSubCategory;

    @Column(name = "REQUESTED_PRICE")
    private float requestedPrice;

    @Column(name = "REQUESTED_QTY")
    private Long requestedQty;

    public AppointmentRequestedServiceSubCategories(AppointmentServiceSubCategoryDTO serviceSubCategory, AppointmentRequestedServiceCategories serviceCategory) {

        if (hasValue(serviceSubCategory.getAppointmentRequestedServiceSubCategoryId()) && serviceSubCategory.getAppointmentRequestedServiceSubCategoryId() > 0) {
            this.id = serviceSubCategory.getAppointmentRequestedServiceSubCategoryId();
        }
        this.appointmentRequestedServiceCategories = serviceCategory;
        log.debug("serviceSubCategory Id = {}", serviceSubCategory.getServiceSubCategoryId());
        this.serviceItemSubCategory = new ServiceItemSubCategory(serviceSubCategory.getServiceSubCategoryId());
        this.requestedPrice = serviceSubCategory.getReqPrice();
        this.requestedQty = serviceSubCategory.getReqQty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentRequestedServiceSubCategories that = (AppointmentRequestedServiceSubCategories) o;
        return Float.compare(that.requestedPrice, requestedPrice) == 0 && Objects.equals(id, that.id) && Objects.equals(serviceItemSubCategory, that.serviceItemSubCategory) && Objects.equals(requestedQty, that.requestedQty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serviceItemSubCategory, requestedPrice, requestedQty);
    }

    @JsonIgnore
    public Long updateStatusAndGetId(int activeStatus) {
        this.setActiveStatus(activeStatus);
        return this.id;
    }

    @JsonIgnore
    public void updateFieldsFromRequest(AppointmentServiceSubCategoryDTO serviceSubCategory, AppointmentRequestedServiceCategories serviceCategory) {
        if (hasValue(serviceSubCategory.getAppointmentRequestedServiceSubCategoryId()) && serviceSubCategory.getAppointmentRequestedServiceSubCategoryId() > 0)
            this.id = serviceSubCategory.getAppointmentRequestedServiceSubCategoryId();
        this.appointmentRequestedServiceCategories = serviceCategory;
        log.debug("serviceSubCategory Id = {}", serviceSubCategory.getServiceSubCategoryId());
        this.serviceItemSubCategory = new ServiceItemSubCategory(serviceSubCategory.getServiceSubCategoryId());
        this.requestedPrice = serviceSubCategory.getReqPrice();
        this.requestedQty = serviceSubCategory.getReqQty();
    }
}
