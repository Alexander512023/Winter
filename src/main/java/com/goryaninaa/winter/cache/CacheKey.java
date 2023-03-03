package com.goryaninaa.winter.cache;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class represents key which you should use to search elements in cache in
 * order to match strategy architecture pattern.
 *
 * @author Alex Goryanin
 */
public class CacheKey {

  private final Object key;
  private final String operationType;
  private final AtomicInteger numberOfUses = new AtomicInteger(1);

  public CacheKey(final Object key, final String operationType) {
    this.key = key;
    this.operationType = operationType;
  }

  public Object getKey() {
    return key;
  }

  public String getOperationType() {
    return operationType;
  }

  public void use() {
    numberOfUses.incrementAndGet();
  }

  public int getNumberOfUses() {
    return numberOfUses.get();
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, operationType);
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
    final CacheKey other = (CacheKey) obj;
    return Objects.equals(key, other.key) && Objects.equals(operationType, other.operationType);
  }
}
