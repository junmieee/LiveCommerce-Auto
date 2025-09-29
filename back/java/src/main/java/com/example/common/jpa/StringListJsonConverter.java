package com.example.common.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final TypeReference<List<String>> TYPE_REFERENCE = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return "[]";
    }
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to convert image key list to JSON", e);
    }
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) {
      return new ArrayList<>();
    }
    try {
      return objectMapper.readValue(dbData, TYPE_REFERENCE);
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to parse image key JSON", e);
    }
  }
}
