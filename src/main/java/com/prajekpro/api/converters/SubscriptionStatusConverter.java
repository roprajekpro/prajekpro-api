package com.prajekpro.api.converters;

import com.prajekpro.api.enums.SubscriptionStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class SubscriptionStatusConverter implements AttributeConverter<SubscriptionStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SubscriptionStatus status) {
        if (null == status) {
            return null;
        }
        return status.value();
    }

    @Override
    public SubscriptionStatus convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(SubscriptionStatus.values())
                .filter(s -> s.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}

