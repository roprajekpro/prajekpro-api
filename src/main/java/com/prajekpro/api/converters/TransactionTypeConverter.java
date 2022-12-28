package com.prajekpro.api.converters;

import com.prajekpro.api.enums.TransactionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<TransactionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TransactionType type) {
        if (null == type) {
            return null;
        }
        return type.value();
    }

    @Override
    public TransactionType convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(TransactionType.values())
                .filter(t -> t.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
