package com.grocky.entity;

import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.converter.spi.BasicValueConverter;

import java.util.Map;

/**
 * Custom converter for JSONB type in PostgreSQL
 */
public class JsonBinaryType implements BasicValueConverter<String, Object> {
    
    @Override
    public Object toDomainValue(String jdbcValue) {
        return jdbcValue;
    }
    
    @Override
    public String toJdbcValue(Object domainValue) {
        if (domainValue == null) {
            return null;
        }
        return domainValue.toString();
    }
    
    @Override
    public Class<String> getJdbcJavaType() {
        return String.class;
    }
    
    @Override
    public int getJdbcTypeCode() {
        return SqlTypes.JSON;
    }
}
