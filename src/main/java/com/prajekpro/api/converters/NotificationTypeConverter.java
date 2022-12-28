package com.prajekpro.api.converters;


import com.prajekpro.api.enums.NotificationType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class NotificationTypeConverter  implements AttributeConverter<NotificationType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(NotificationType type) {
        if (null == type) {
            return null;
        }
        return type.value();
    }

    @Override
    public NotificationType convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(NotificationType.values())
                .filter(nt -> nt.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}

