package com.prajekpro.api.domain;

import com.prajekpro.api.enums.*;
import com.safalyatech.common.domains.*;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "coupon_code_details")
public class CouponCodeDetails extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String couponCode;
    private int validityPerUser;
    private CouponCodeType couponCodeType;
    private String metaData;
}
