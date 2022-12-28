package com.prajekpro.api.converters;

import com.prajekpro.api.enums.WalletAmountType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class WalletAmountTypeConverter implements AttributeConverter<WalletAmountType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(WalletAmountType type) {
        if (null == type) {
            return null;
        }
        return type.value();
    }

    @Override
    public WalletAmountType convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(WalletAmountType.values())
                .filter(t -> t.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
