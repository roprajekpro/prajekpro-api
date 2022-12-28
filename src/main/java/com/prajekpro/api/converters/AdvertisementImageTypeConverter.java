package com.prajekpro.api.converters;

import com.prajekpro.api.enums.AdvertisementImageType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AdvertisementImageTypeConverter implements AttributeConverter<AdvertisementImageType, Integer> {

@Override
public Integer convertToDatabaseColumn(AdvertisementImageType type) {
        if (null == type) {
        return null;
        }
        return type.value();
        }

@Override
public AdvertisementImageType convertToEntityAttribute(Integer value) {
        if (null == value) {
        return null;
        }

        return Stream.of(AdvertisementImageType.values())
        .filter(at -> at.value() == value)
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
        }

        }

