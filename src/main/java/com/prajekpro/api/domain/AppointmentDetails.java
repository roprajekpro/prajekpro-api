package com.prajekpro.api.domain;

import com.prajekpro.api.enums.*;
import com.safalyatech.common.domains.*;
import com.safalyatech.common.dto.*;
import lombok.*;

import javax.persistence.*;
import java.io.*;
import java.util.*;


@Entity
@Getter
@Setter
@NoArgsConstructor

@Table(name = "appointment_details")
public class AppointmentDetails extends Auditable implements Serializable {

    private static final long serialVersionUID = 7252076323291507168L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "BOOKED_BY")
    private Users bookedBy;

    @ManyToOne
    @JoinColumn(name = "BOOKED_FOR")
    private ProDetails bookedFor;

    @OneToOne
    @JoinColumn(name = "FK_USER_DELIVERY_ADDRESS_ID")
    private UserDeliveryAddress userDeliveryAddress;

    @Column(name = "STATE")
    private AppointmentState state;

    @Column(name = "CANCELLED_BY")
    private String cancelledBy;

    @ManyToOne
    @JoinColumn(name = "CANCELLED_REMARK_ID")
    private PPLookUp cancelledRemark;

    @Column(name = "CANCELLED_REMARKS")
    private String cancelledRemarksDesc;

    @Column(name = "CUSTOMER_SIGN_DISPLAY_NM")
    private String customerSignDisplayNm;

    @Column(name = "CUSTOMER_SIGN_SAVED_NM")
    private String customerSignSavedNm;

    @Column(name = "CUSTOMER_SIGN_EXTN")
    private String customerSignExtn;

    @Column(name = "CUSTOMER_SIGN_SAVE_DIR_NM")
    private String customerSignSaveDirNm;

    @Column(name = "SUB_TOTAL")
    private Double subTotal;

    @Column(name = "CANC_PNLTY_LOCK_AMOUNT")
    private Double cancellationPnltyLockedAmount;

    @Column(name = "PRAJEKPRO_LOCK_AMOUNT")
    private Double prajekProLockedAmount;

    @Column(name = "GRAND_TOTAL")
    private Double grandTotal;

    @OneToMany(targetEntity = AppointmentRequestedServices.class, mappedBy = "appointmentDetails", cascade = CascadeType.ALL)
    private List<AppointmentRequestedServices> appointmentRequestedServices;

    @OneToOne(targetEntity = AppointmentInvoice.class, mappedBy = "appointmentDetails", cascade = CascadeType.ALL)
    private AppointmentInvoice appointmentInvoice;

    @OneToMany(targetEntity = AppointmentOtherServices.class, mappedBy = "appointmentDetails", cascade = CascadeType.ALL)
    private List<AppointmentOtherServices> appointmentOtherServices;

    @OneToMany(targetEntity = AppointmentTaxDetails.class, mappedBy = "appointmentDetails", cascade = CascadeType.ALL)
    private List<AppointmentTaxDetails> appointmentTaxDetailsList;

    @Transient
    private int jobsCompleted;

    public void updateCustomerSignDetails(FileDetailsDTO fileDetailsDTO, String filePath) {
        this.customerSignSaveDirNm = filePath;
        this.customerSignSavedNm = fileDetailsDTO.getFileSavedName();
        this.customerSignExtn = fileDetailsDTO.getFileExtension();
        this.customerSignDisplayNm = fileDetailsDTO.getFileName();
    }
}
