package com.prajekpro.api.dto;

import com.prajekpro.api.domain.*;
import lombok.*;
import lombok.extern.slf4j.*;

import static com.safalyatech.common.utility.CheckUtil.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Slf4j
public class UserDeliveryAddressDTO {

    private Long addressId;
    private String userId;
    private String addressLine;
    private Long addressTypeId;
    private String addressType;
    private String houseFlatBlockNo;
    private String landmark;
    private Double latitude;
    private Double longitude;

    public UserDeliveryAddressDTO(UserDeliveryAddress userDeliveryAddress) {
        this.addressId = userDeliveryAddress.getId();
        this.userId = userDeliveryAddress.getUsers().getUserId();
        this.addressLine = userDeliveryAddress.getAddressLine();
        this.addressTypeId = userDeliveryAddress.getAddressType().getId();
        this.addressType = userDeliveryAddress.getAddressType().getValue();
        this.houseFlatBlockNo = userDeliveryAddress.getHouseFlatBlockNo();
        this.landmark = userDeliveryAddress.getLandmark();
        this.latitude = userDeliveryAddress.getLatitude();
        this.longitude = userDeliveryAddress.getLongitude();
    }

    public String getAddressString() {
        return getValue(houseFlatBlockNo) + getValue(landmark) + addressLine;
    }

    private String getValue(String value) {
        return hasValue(value) ? value + ", " : "";
    }
}
