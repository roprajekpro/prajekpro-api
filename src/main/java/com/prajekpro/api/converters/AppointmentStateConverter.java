package com.prajekpro.api.converters;

import com.prajekpro.api.enums.AppointmentState;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AppointmentStateConverter implements AttributeConverter<AppointmentState, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AppointmentState state) {
        if (null == state) {
            return null;
        }
        return state.value();
    }

    @Override
    public AppointmentState convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(AppointmentState.values())
                .filter(s -> s.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
