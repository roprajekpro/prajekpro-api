package com.prajekpro.api.enums;

import javax.persistence.*;
import java.util.stream.*;

@Converter(autoApply = true)
public class CouponCodeTypeConverter implements AttributeConverter<CouponCodeType, Long> {

    @Override
    public Long convertToDatabaseColumn(CouponCodeType couponCodeType) {
        if (null == couponCodeType) {
            return null;
        }
        return couponCodeType.value();
    }

    @Override
    public CouponCodeType convertToEntityAttribute(Long value) {
        if (null == value) {
            return null;
        }

        return Stream.of(CouponCodeType.values())
                .filter(cct -> cct.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
