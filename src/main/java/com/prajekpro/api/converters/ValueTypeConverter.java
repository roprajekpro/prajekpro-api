package com.prajekpro.api.converters;

import com.prajekpro.api.enums.ValueType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ValueTypeConverter implements AttributeConverter<ValueType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ValueType type) {
        if (null == type) {
            return null;
        }
        return type.value();
    }

    @Override
    public ValueType convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(ValueType.values())
                .filter(t -> t.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
