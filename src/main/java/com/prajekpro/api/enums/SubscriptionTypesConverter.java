package com.prajekpro.api.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class SubscriptionTypesConverter implements AttributeConverter<SubscriptionTypes, Integer> {
    @Override
    public Integer convertToDatabaseColumn(SubscriptionTypes subscriptionType) {
        if (null == subscriptionType) {
            return null;
        }
        return subscriptionType.value();
    }

    @Override
    public SubscriptionTypes convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(SubscriptionTypes.values())
                .filter(st -> st.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
