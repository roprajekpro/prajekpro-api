package com.prajekpro.api.converters;

import com.prajekpro.api.enums.MessageType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class MessageTypeConverter implements AttributeConverter<MessageType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(MessageType type) {
        if (null == type) {
            return null;
        }
        return type.value();
    }

    @Override
    public MessageType convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(MessageType.values())
                .filter(t -> t.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}

