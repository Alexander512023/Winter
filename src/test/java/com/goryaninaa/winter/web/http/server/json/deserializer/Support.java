package com.goryaninaa.winter.web.http.server.json.deserializer;

import java.util.Objects;

/**
 * Test entity.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("unused")
public class Support { // NOPMD
  private String url;
  private String text;

  public Support() { // NOPMD
  }

  /**
   * Test entity constructor.
   *
   * @param url  - url
   * @param text - text
   */
  public Support(final String url, final String text) {
    super();
    this.url = url;
    this.text = text;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(final String url) {
    this.url = url;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, url);
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
    final Support other = (Support) obj;
    return Objects.equals(text, other.text) && Objects.equals(url, other.url);
  }

}
