package com.prajekpro.api.domain;

import com.prajekpro.api.dto.UserDeliveryAddressDTO;
import com.safalyatech.common.domains.Auditable;
import com.safalyatech.common.domains.PPLookUp;
import com.safalyatech.common.domains.Users;
import com.safalyatech.common.utility.CheckUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "user_delivery_address")
public class UserDeliveryAddress extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "FK_USER_ID")
    private Users users;


    @Column(name = "ADDRESS_LINE")
    private String addressLine;

    @ManyToOne
    @JoinColumn(name = "ADDRESS_TYPE")
    private PPLookUp addressType;

    @Column(name = "HOUSE_FLAT_SHOP_BLOCK_NO")
    private String houseFlatBlockNo;

    @Column(name = "LANDMARK")
    private String landmark;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")
    private Double longitude;

    public UserDeliveryAddress(UserDeliveryAddressDTO userAddress, String userId) {
        if(!CheckUtil.hasValue(userAddress.getAddressId())){
            this.id = userAddress.getAddressId();
        }

        this.addressLine = userAddress.getAddressLine();
        this.houseFlatBlockNo = userAddress.getHouseFlatBlockNo();
        this.landmark = userAddress.getLandmark();
        this.latitude = userAddress.getLatitude();
        this.longitude = userAddress.getLongitude();

         Users user = new Users(userId);
         this.users = user;

         PPLookUp addressType = new PPLookUp(userAddress.getAddressTypeId());
         this.addressType = addressType;

    }

    public UserDeliveryAddress(Long userAddressDetailsId) {
        this.id = userAddressDetailsId;
    }
}
