package com.goryaninaa.winter.web.http.server.json;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.web.http.server.exception.ServerException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Recursive object to JSON generic converter.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("StringBufferMayBeStringBuilder")
public class JsonSerializer { // NOPMD

  private static final Logger LOG = LoggingMech.getLogger(JsonSerializer.class.getCanonicalName());
  private static final String VAL_FOR_NULL = "null";

  /**
   * Receives generic object and transform it to String of JSON format.
   */
  public <T> String serialize(final T responseObject) {
    String body;
    body = getStringRepresentation(responseObject);
    return body;
  }

  private <T> String getStringRepresentation(final T object) {
    final StringBuffer result = new StringBuffer();
    if (object.getClass().isEnum()) {
      result.append('\"').append(object).append('\"');
    } else {
      final Map<String, Type> fieldTypeMap = collectFieldTypeMap(object.getClass());
      final Map<String, String> fieldValueMap = collectFieldValueMap(object, fieldTypeMap);
      result.append(wrap(fieldValueMap));
    }
    return result.toString();
  }

  private <T> Map<String, Type> collectFieldTypeMap(final Class<? extends T> clazz) {
    final Map<String, Type> fieldTypeMap = new LinkedHashMap<>(15, 0.75f, false); // NOPMD
    final Field[] fields = clazz.getDeclaredFields();
    for (final Field field : fields) {
      final String name = field.getName();
      final Type type = field.getType();
      fieldTypeMap.put(name, type);
    }
    return fieldTypeMap;
  }

  private <T> Map<String, String> collectFieldValueMap(final T object,
      final Map<String, Type> fieldTypeMap) {
    final Map<String, String> fieldValueMap = new LinkedHashMap<>(15, 0.75f, false); // NOPMD
    for (final Entry<String, Type> fieldType : fieldTypeMap.entrySet()) {
      final String name = fieldType.getKey();
      final Type type = fieldType.getValue();
      final String value = getFieldValue(object, name, type);
      fieldValueMap.put(name, value);
    }
    return fieldValueMap;
  }

  private <T> String getFieldValue(final T object, final String name, final Type type) {
    String fieldValue;
    try {
      final String methodName = defineMethodName(name, type);
      final Method getter = object.getClass().getDeclaredMethod(methodName);
      fieldValue = defineFieldValue(object, type, getter);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Serialization failed");
      }
      throw new ServerException("Serialization failed", e);
    }
    return fieldValue;
  }

  private <T> String defineFieldValue(final T object, final Type type, final Method getter) // NOPMD
      throws IllegalAccessException, InvocationTargetException {
    String fieldValue;
    if (type.equals(int.class) || type.equals(double.class) || type.equals(Boolean.class)) {
      fieldValue = valueOfPrimitive(object, getter);
    } else if (type.equals(String.class) || type.equals(LocalDate.class) || type.equals(Date.class)
        || type.equals(LocalDateTime.class) || type.equals(Integer.class)) {
      fieldValue = valueOfString(object, getter);
    } else if (type.equals(List.class)) {
      fieldValue = valueOfList(object, getter);
    } else {
      fieldValue = valueOfObject(object, getter);
    }
    return fieldValue;
  }

  private <T> String valueOfObject(final T object, final Method getter)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { // NOPMD
    String result;
    final Object fieldObject = getter.invoke(object);
    if (fieldObject != null) {
      result = getStringRepresentation(fieldObject);
    } else {
      result = VAL_FOR_NULL;
    }
    return result;
  }

  private <T> String valueOfList(final T object, final Method getter)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { // NOPMD
    String result;
    final List<?> fieldList = (List<?>) getter.invoke(object, new Object[0]);
    if (fieldList != null) {
      result = wrap(fieldList);
    } else {
      result = VAL_FOR_NULL;
    }
    return result;
  }

  private <T> String valueOfString(final T object, final Method getter)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { // NOPMD
    final StringBuilder result = new StringBuilder();
    final String value = String.valueOf(getter.invoke(object));
    if (VAL_FOR_NULL.equals(value)) {
      result.append(VAL_FOR_NULL);
    } else {
      result.append('\"').append(value).append('\"');
    }
    return result.toString();
  }

  private <T> String valueOfPrimitive(final T object, final Method getter)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException { // NOPMD
    return String.valueOf(getter.invoke(object));
  }

  private String defineMethodName(final String name, final Type type) {
    final StringBuffer result = new StringBuffer();
    if (type.equals(Boolean.class)) {
      result.append("is").append(name.substring(0, 1).toUpperCase(Locale.ROOT))
          .append(name.substring(1));
    } else {
      result.append("get").append(name.substring(0, 1).toUpperCase(Locale.ROOT))
          .append(name.substring(1));
    }
    return result.toString();
  }

  private String wrap(final Map<String, String> fieldValueMap) {
    StringBuffer result = new StringBuffer("{");
    for (final Entry<String, String> fieldValue : fieldValueMap.entrySet()) {
      result.append('\"').append(fieldValue.getKey()).append("\": ").append(fieldValue.getValue())
          .append(',');
    }
    result = new StringBuffer(result.toString().substring(0, result.length() - 1)).append('}');
    return result.toString();
  }

  private <T> String wrap(final List<T> fieldList) {
    StringBuffer result = new StringBuffer("[");
    for (final T value : fieldList) {
      final Type valueType = value.getClass();
      if (valueType.equals(int.class) || valueType.equals(double.class)
          || valueType.equals(Boolean.class)) {
        result.append(value).append(',');
      } else if (valueType.equals(String.class)) {
        result.append(" \"").append(value).append("\",");
      } else {
        result.append(getStringRepresentation(value)).append(',');
      }
    }
    result = new StringBuffer(result.toString().substring(0, result.length() - 1)).append(']');
    return result.toString();
  }
}
