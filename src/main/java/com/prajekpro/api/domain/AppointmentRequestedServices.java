package com.prajekpro.api.domain;


import com.prajekpro.api.dto.AppointmentServicesDTO;
import com.prajekpro.api.dto.CommonFieldsDTO;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.utility.CheckUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor

@Table(name = "appointment_requested_services")
public class AppointmentRequestedServices extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_MASTER_ID")
    private AppointmentDetails appointmentDetails;

    @ManyToOne
    @JoinColumn(name = "FK_SERVICE_ID")
    private Services services;

    @Column(name = "APPOINTMENT_DATE")
    private String appointmentDate;

    @Column(name = "TIME_SLOT_ID")
    private Long timeSlotId;

    @Column(name = "APPOINTMENT_TIME")
    private String appointmentTime;

    @Column(name = "CHECK_IN_TIME")
    private LocalTime checkInTime;

    @Column(name = "CHECK_OUT_TIME")
    private LocalTime checkOutTime;


    @OneToMany(targetEntity = AppointmentRequestedServiceCategories.class,mappedBy = "appointmentRequestedServices", cascade = CascadeType.ALL)
    private List<AppointmentRequestedServiceCategories> appointmentRequestedServiceCategories;

    public AppointmentRequestedServices(AppointmentServicesDTO services, AppointmentDetails appointmentDetails) {

        if(CheckUtil.hasValue(services.getAppointmentRequestedServiceId()) && services.getAppointmentRequestedServiceId() > 0){
            this.id = services.getAppointmentRequestedServiceId();
        }
        appointmentDate = services.getDate();
        CommonFieldsDTO timeSlot = services.getTimeSlot();
        timeSlotId = timeSlot.getId();
        appointmentTime = timeSlot.getValue();
        this.services = new Services(services.getServiceId());
        this.appointmentDetails = appointmentDetails;
    }


}
