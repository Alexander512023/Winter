package com.goryaninaa.web.http.server.json.serializer;

import java.util.List;

/**
 * Test DTO entity.
 *
 * @author Alex Goryanin
 */
public class Sensor { // NOPMD

  private String name;
  private List<Measurement> measurements;

  public Sensor() { // NOPMD
  }

  /**
   * Test DTO entity constructor.
   *
   * @param name         - name
   * @param measurements - measurements
   */
  public Sensor(final String name, final List<Measurement> measurements) {
    super();
    this.name = name;
    this.measurements = measurements;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public List<Measurement> getMeasurements() {
    return measurements;
  }

  public void setMeasurements(final List<Measurement> measurements) {
    this.measurements = measurements;
  }

}
