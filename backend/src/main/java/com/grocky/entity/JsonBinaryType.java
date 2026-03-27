package com.grocky.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Custom converter for JSONB type in PostgreSQL (Hibernate 6 compatible)
 */
@Converter(autoApply = true)
public class JsonBinaryType implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
