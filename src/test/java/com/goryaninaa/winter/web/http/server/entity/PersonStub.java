package com.goryaninaa.winter.web.http.server.entity;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("unused")
public class PersonStub {

  private String name;

  /**
   * Default constructor.
   *
   */
  public PersonStub() { // NOPMD
  }

  /**
   * Constructor with name parameter.
   *
   * @param name - name parameter
   */
  public PersonStub(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

}
