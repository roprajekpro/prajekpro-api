package com.prajekpro.api.converters;

import com.prajekpro.api.enums.AdvertisementType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AdvertisementTypeConverter implements AttributeConverter<AdvertisementType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AdvertisementType type) {
        if (null == type) {
            return null;
        }
        return type.value();
    }

    @Override
    public AdvertisementType convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(AdvertisementType.values())
                .filter(at -> at.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}

