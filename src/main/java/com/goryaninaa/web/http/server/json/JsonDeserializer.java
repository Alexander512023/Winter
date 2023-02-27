package com.goryaninaa.web.http.server.json;

import com.goryaninaa.logger.mech.Logger;
import com.goryaninaa.logger.mech.LoggingMech;
import com.goryaninaa.web.http.server.exception.ClientException;
import com.goryaninaa.web.http.server.exception.ServerException;
import com.goryaninaa.web.http.server.request.handler.Deserializer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recursive JSON to object generic converter.
 *
 * @author Alex Goryanin
 */
public class JsonDeserializer implements Deserializer { // NOPMD

  private static final Logger LOG = LoggingMech
      .getLogger(JsonDeserializer.class.getCanonicalName());

  /**
   * Overriden method of {@link Deserializer} interface. Receives desired Class
   * and String that came in body of HTTP request. Source information than
   * transforms to the instance of desired type.
   */
  @Override
  public <T> T deserialize(final Class<T> clazz, final String jsonString) {
    checkJsonFormat(jsonString);
    final String[] jsonLines = extractLines(jsonString);
    final Map<String, String> methodNameValueStringMap = // NOPMD
        splitJsonLinesToStringMap(jsonLines);
    final Map<Method, Object> methodValueMap = convertToMethodValueMap(clazz,
        methodNameValueStringMap);
    return createInstance(clazz, methodValueMap);
  }

  private <T> T createInstance(final Class<T> clazz, final Map<Method,
      Object> methodNameValueMap) { // NOPMD
    try {
      final T instance = clazz.getDeclaredConstructor().newInstance();
      enrichInstanceWithData(instance, methodNameValueMap);
      return instance;
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Failed to create instance");
      }
      throw new ServerException("Failed to create instance", e);
    }
  }

  private <T> void enrichInstanceWithData(final T instance,
      final Map<Method, Object> methodNameValueMap) { // NOPMD
    for (final Entry<Method, Object> methodNameValue : methodNameValueMap.entrySet()) {
      final Method method = methodNameValue.getKey();
      final Object parameter = methodNameValue.getValue();
      try {
        method.invoke(instance, parameter);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        if (LOG.isErrorEnabled()) {
          LOG.error("Failed to enrich instance with data");
        }
        throw new ServerException("Failed to enrich instance with data", e);
      }
    }
  }

  private <T> Map<Method, Object> convertToMethodValueMap(final Class<T> clazz,
      final Map<String, String> methodNameValueStringMap) { // NOPMD
    final Map<Method, Object> methodNameValueMap = new HashMap<>(); // NOPMD
    final Method[] methods = clazz.getDeclaredMethods();
    for (final Entry<String, String> methodNameValue : methodNameValueStringMap.entrySet()) {
      final String methodName = methodNameValue.getKey();
      final String valueString = methodNameValue.getValue();
      final Method method = defineMethod(methods, methodName);
      final Object value = getRealValue(method, valueString);
      methodNameValueMap.put(method, value);
    }
    return methodNameValueMap;
  }

  private Object getRealValue(final Method method, final String valueString) {
    Object realValueObject = null; // NOPMD
    final Class<?> clazz = (Class<?>) method.getParameterTypes()[0];
    final String firstSymbol = valueString.substring(0, 1);
    if ("{".equals(firstSymbol)) {
      realValueObject = deserialize(clazz, valueString);
    } else if ("[".equals(firstSymbol)) {
      final ParameterizedType elementType = (ParameterizedType) method
          .getGenericParameterTypes()[0];
      realValueObject = ofList(elementType, valueString);
    } else if ("\"".equals(firstSymbol)) {
      realValueObject = ofStringOrConst(clazz, valueString);
    } else {
      realValueObject = ofPrimitive(clazz, valueString);
    }
    return realValueObject;
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> ofList(final ParameterizedType elementType, final String valueString) {
    final List<T> resArr = new ArrayList<>();
    final Class<T> clazz = (Class<T>) elementType.getActualTypeArguments()[0];
    final String[] elementLines = extractLines(valueString);
    for (final String elementLine : elementLines) {
      resArr.add((T) deserialize(clazz, elementLine));
    }
    return resArr;
  }

  private Object ofPrimitive(final Class<?> clazz, final String valueString) {
    Object primitiveObj;
    if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
      primitiveObj = Integer.valueOf(valueString);
    } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
      primitiveObj = Double.valueOf(valueString);
    } else if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
      primitiveObj = Boolean.valueOf(valueString);
    } else {
      throw new IllegalArgumentException("Value of unsupported primitive type");
    }
    return primitiveObj;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object ofStringOrConst(final Class<?> clazz, final String valueString) {
    Object stringObj;
    final String trimedValueString = removeExternalSymbols(valueString);
    if (clazz.isEnum()) {
      try {
        stringObj = Enum.valueOf((Class<? extends Enum>) Class.forName(clazz.getCanonicalName()),
            String.valueOf(trimedValueString));
      } catch (ClassNotFoundException e) {
        throw new ServerException("Class not found", e);
      }
    } else {
      stringObj = trimedValueString;
    }
    return stringObj;
  }

  private Method defineMethod(final Method[] methods, final String methodName) {
    for (final Method method : methods) {
      if (method.getName().equals(methodName)) {
        method.setAccessible(true); // NOPMD
        return method;
      }
    }
    throw new IllegalArgumentException("There is no such set method");
  }

  private Map<String, String> splitJsonLinesToStringMap(final String... jsonLines) {
    final Map<String, String> stringMap = new HashMap<>(); // NOPMD
    final Pattern pattern = Pattern.compile("\".+?\"\\s*:");
    for (final String jsonLine : jsonLines) {
      final Matcher matcher = pattern.matcher(jsonLine);
      matcher.find();
      final String fieldWithQuotes = jsonLine.substring(0, matcher.end() - 1).trim();
      final String field = removeExternalSymbols(fieldWithQuotes);
      final String methodName = convertFieldToMethodName(field);
      final String value = jsonLine.substring(matcher.end()).trim();
      stringMap.put(methodName, value);
    }
    return stringMap;
  }

  private String convertFieldToMethodName(final String fieldName) {
    final Pattern pattern = Pattern.compile("_");
    final Matcher matcher = pattern.matcher(fieldName);
    final StringBuffer setMethodName = new StringBuffer("set");
    String estimated = fieldName;
    while (matcher.find()) {
      setMethodName.append(estimated.substring(0, 1).toUpperCase(Locale.ROOT))
          .append(estimated.substring(1, matcher.start()));
      estimated = estimated.substring(matcher.start() + 1);
    }
    return setMethodName.append(fieldName.substring(0, 1).toUpperCase(Locale.ROOT))
        .append(fieldName.substring(1)).toString();
  }

  private String[] returnCommasToLines(String... jsonLines) {
    for (int i = 0; i < jsonLines.length; i++) {
      jsonLines[i] = returnCommas(jsonLines[i]);
    }
    return jsonLines;
  }

  private String returnCommas(final String jsonLine) {
    final StringBuffer res = new StringBuffer();
    int pos = 0;
    final Pattern pattern = Pattern.compile("\\[comma\\]");
    final Matcher matcher = pattern.matcher(jsonLine);
    while (matcher.find()) {
      res.append(jsonLine.substring(pos, matcher.start())).append(',');
      pos = matcher.end();
    }
    res.append(jsonLine.substring(pos, jsonLine.length()));
    return res.toString();
  }

  private String[] splitJsonToLines(final String jsonString) {
    return jsonString.split(",");
  }

  private String replaceExcessCommas(final String jsonString) {
    final StringBuffer res = new StringBuffer();
    int pos = 0;
    final Pattern pattern = Pattern.compile("((?s)\\{.*?\\})|(\".*?\")|((?s)\\[.*?\\])");
    final Matcher matcher = pattern.matcher(jsonString);
    while (matcher.find()) {
      if (jsonString.substring(matcher.start(), matcher.end()).contains(",")) {
        res.append(jsonString.substring(pos, matcher.start()))
            .append(replace(jsonString.substring(matcher.start(), matcher.end())));
        pos = matcher.end();
      }
    }
    res.append(jsonString.substring(pos, jsonString.length()));
    return res.toString();
  }

  private String replace(final String jsonSubstring) {
    final StringBuffer res = new StringBuffer();
    int pos = 0;
    final Pattern pattern = Pattern.compile(",");
    final Matcher matcher = pattern.matcher(jsonSubstring);
    while (matcher.find()) {
      res.append(jsonSubstring.substring(pos, matcher.start())).append("[comma]");
      pos = matcher.end();
    }
    res.append(jsonSubstring.substring(pos, jsonSubstring.length()));
    return res.toString();
  }

  private String removeExternalSymbols(final String jsonString) {
    final String tremmedJsonString = jsonString.trim();
    return tremmedJsonString.substring(1, tremmedJsonString.length() - 1).trim();
  }

  private String[] extractLines(final String jsonString) {
    String processingString = removeExternalSymbols(jsonString);
    processingString = replaceExcessCommas(processingString);
    String[] jsonLines = splitJsonToLines(processingString);
    jsonLines = returnCommasToLines(jsonLines);
    return jsonLines;
  }

  private void checkJsonFormat(final String jsonString) {
    if (jsonString.isEmpty()) {
      throw new ServerException("Empty JSON string");
    }
    final int counter = countLines(jsonString);
    final Pattern pattern = Pattern
        .compile("(?s)\\{(.*?\".+?\".*?:\\s*.+?){" + counter + "}.*?\\}");
    final Matcher matcher = pattern.matcher(jsonString);
    if (!matcher.find() || counter == 0) {
      throw new ClientException("Deserializing JSON incorrect format");
    }
  }

  private int countLines(final String jsonString) {
    int counter = 0;
    final Pattern patternCounter = Pattern.compile("\".+?\".*?:\\s*.?");
    final Matcher matcherCounter = patternCounter.matcher(jsonString);
    while (matcherCounter.find()) {
      counter++;
    }
    return counter;
  }
}
