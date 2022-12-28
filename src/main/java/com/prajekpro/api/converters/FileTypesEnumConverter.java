package com.prajekpro.api.converters;

import com.prajekpro.api.enums.FileTypes;

import javax.persistence.AttributeConverter;
import java.util.stream.Stream;

public class FileTypesEnumConverter implements AttributeConverter<FileTypes, Integer> {

    @Override
    public Integer convertToDatabaseColumn(FileTypes fileTypes) {
        if (null == fileTypes) {
            return null;
        }
        return fileTypes.value();
    }

    @Override
    public FileTypes convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(FileTypes.values())
                .filter(as -> as.value().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}