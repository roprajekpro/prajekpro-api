package com.prajekpro.api.domain;

import com.safalyatech.common.domains.*;
import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "coupon_redemption_details")
public class CouponRedemptionDetails extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "COUPON_CODE_DETAILS_ID")
    private CouponCodeDetails couponCodeDetails;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private Users user;

    public CouponRedemptionDetails(CouponCodeDetails couponCodeDetails, Users user) {
        this.couponCodeDetails = couponCodeDetails;
        this.user = user;
    }
}
