package com.prajekpro.api.enums;

import javax.persistence.*;
import java.util.stream.*;

@Converter(autoApply = true)
public class DocTypeConverter implements AttributeConverter<DocType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DocType docType) {
        if (null == docType) {
            return null;
        }
        return docType.value();
    }

    @Override
    public DocType convertToEntityAttribute(Integer value) {
        if (null == value) {
            return null;
        }

        return Stream.of(DocType.values())
                .filter(dt -> dt.value() == value)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
