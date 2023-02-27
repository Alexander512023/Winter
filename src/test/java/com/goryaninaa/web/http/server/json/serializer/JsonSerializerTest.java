package com.goryaninaa.web.http.server.json.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.goryaninaa.web.http.server.json.JsonSerializer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonSerializerTest {

  private Sensor sensor;

  @BeforeEach
  void init() {
    final Measurement measurement1 = new Measurement(15.5, true);
    final Measurement measurement2 = new Measurement(30.5, false);
    final List<Measurement> measurements = new ArrayList<>();
    measurements.add(measurement1);
    measurements.add(measurement2);
    this.sensor = new Sensor("Sensor", measurements);
  }

  @Test
  void testSerialize() {
    final JsonSerializer serializer = new JsonSerializer();
    final String actual = serializer.serialize(sensor);
    final String expected = "{\"name\": \"Sensor\",\"measurements\": "
        + "[{\"value\": 15.5,\"raining\": true},{\"value\": 30.5,\"raining\": false}]}";
    assertEquals(expected, actual);
  }

}
