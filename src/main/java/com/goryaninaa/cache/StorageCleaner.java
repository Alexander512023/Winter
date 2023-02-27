package com.goryaninaa.cache;

import com.goryaninaa.logger.mech.Logger;
import com.goryaninaa.logger.mech.LoggingMech;
import com.goryaninaa.logger.mech.StackTraceString;
import java.util.Properties;
import java.util.concurrent.Executors;

/**
 * This class is responsible for cache clean up logic.
 *
 * @author Alex Goryanin
 * @param <V> - entity that will be cached
 */
public class StorageCleaner<V> {

  private final Storage<V> storage;
  private final int sizeParam;
  private final int underused;
  private static final Logger LOG = LoggingMech.getLogger(StorageCleaner.class.getCanonicalName());

  /**
   * Constructor.
   *
   * @param storage - storage
   * @param properties - properties
   */
  public StorageCleaner(final Storage<V> storage, final Properties properties) {
    this.storage = storage;
    this.sizeParam = Integer.valueOf(properties.getProperty("Cache.size"));
    this.underused = Integer.valueOf(properties.getProperty("Cache.underused"));
  }

  public void run() {
    Executors.newSingleThreadExecutor().submit(() -> cleanUp());
  }

  private void cleanUp() {
    while (true) {
      cleanCache();
    }
  }

  private void cleanCache() {
    if (storage.size() > sizeParam) {
      if (storage.size() - storage.countUnderused() < sizeParam) {
        storage.cleanBelow(underused);
      } else {
        storage.cleanBelow(storage.defineMedian());
      }
    }
    sleep(1000);
  }

  private void sleep(final int miliseconds) {
    try {
      Thread.sleep(miliseconds);
    } catch (InterruptedException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error(StackTraceString.get(e));
      }
      throw new CacheException("Cache clean up failed", e);
    }
  }
}
