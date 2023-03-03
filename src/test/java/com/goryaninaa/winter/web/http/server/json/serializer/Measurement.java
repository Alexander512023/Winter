package com.goryaninaa.winter.web.http.server.json.serializer;

/**
 * Test DTO entity.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("unused")
public class Measurement { // NOPMD

  private double value;

  private Boolean raining;

  public Measurement() {
    super();
  }

  /**
   * Test DTO entity constructor.
   *
   * @param value   - value
   * @param raining - raining
   */
  public Measurement(final double value, final boolean raining) {
    super();
    this.value = value;
    this.raining = raining;
  }

  public double getValue() {
    return value;
  }

  public void setValue(final double value) {
    this.value = value;
  }

  public boolean isRaining() {
    return raining;
  }

  public void setRaining(final boolean raining) {
    this.raining = raining;
  }
}
