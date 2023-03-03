package com.goryaninaa.winter.cache;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;

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
    this.sizeParam = Integer.parseInt(properties.getProperty("Cache.size"));
    this.underused = Integer.parseInt(properties.getProperty("Cache.underused"));
  }

  public void run() {
    Executors.newSingleThreadExecutor().submit(this::cleanUp);
  }

  private void cleanUp() {
    //noinspection InfiniteLoopStatement
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
    sleep();
  }

  private void sleep() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error(StackTraceString.get(e));
      }
      throw new CacheException("Cache clean up failed", e);
    }
  }
}
