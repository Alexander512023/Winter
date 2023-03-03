package com.goryaninaa.winter.web.http.server.json.deserializer;

import java.util.Objects;

/**
 * Test entity.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("unused")
public class Person { // NOPMD
  private int id; // NOPMD
  private String email;
  private String firstName;
  private String lastName;
  private String avatar;

  public Person() {
    super();
  }

  /**
   * Test entity constructor.
   *
   * @param id        - id
   * @param email     - email
   * @param firstName - firstName
   * @param lastName  - lastName
   * @param avatar    - avatar
   */
  public Person(final int id, final String email, final String firstName, // NOPMD
      final String lastName, final String avatar) {
    super();
    this.id = id;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.avatar = avatar;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) { // NOPMD
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(final String avatar) {
    this.avatar = avatar;
  }

  @Override
  public int hashCode() {
    return Objects.hash(avatar, email, firstName, id, lastName);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true; // NOPMD
    }
    if (obj == null) {
      return false; // NOPMD
    }
    if (getClass() != obj.getClass()) {
      return false; // NOPMD
    }
    final Person other = (Person) obj;
    return Objects.equals(avatar, other.avatar) && Objects.equals(email, other.email)
        && Objects.equals(firstName, other.firstName) && id == other.id
        && Objects.equals(lastName, other.lastName);
  }
}