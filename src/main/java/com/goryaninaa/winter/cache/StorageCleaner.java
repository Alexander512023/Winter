package com.goryaninaa.winter.cache;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for cache clean up logic.
 *
 * @author Alex Goryanin
 * @param <V> - entity that will be cached
 */
public class StorageCleaner<V> {

  private final Map<CacheKey, Map<CacheKey, Future<Optional<V>>>> cacheStorage;
  private final int sizeParam;
  private final int underused;
  private final ExecutorService executorService;
  private final AtomicBoolean running;
  private static final Logger LOG = LoggingMech.getLogger(StorageCleaner.class.getCanonicalName());

  /**
   * Constructor.
   *
   * @param storage - storage
   * @param properties - properties
   */
  public StorageCleaner(final Storage<V> storage, final Properties properties) {
    this.cacheStorage = storage.cacheStorage;
    this.sizeParam = Integer.parseInt(properties.getProperty("Cache.size"));
    this.underused = Integer.parseInt(properties.getProperty("Cache.underused"));
    this.executorService = Executors.newSingleThreadExecutor();
    this.running = new AtomicBoolean(false);
  }

  public void run() {
    executorService.submit(this::cleanUp);
    running.set(true);
    if (LOG.isInfoEnabled()) {
      LOG.info("Cache cleaning is running.");
    }
  }

  public void shutdown() {
    if (running.get()) {
      executorService.shutdownNow();
      if (LOG.isInfoEnabled()) {
        LOG.info("Cache cleaning is shutdown.");
      }
    }
  }

  private void cleanUp() {
    //noinspection InfiniteLoopStatement
    while (true) {
      cleanCache();
    }
  }

  private void cleanCache() {
    if (size() > sizeParam) {
      if (size() - countUnderused() < sizeParam) {
        cleanBelow(underused);
      } else {
        cleanBelow(defineMedian());
      }
    }
    sleep();
  }

  private int size() {
    return cacheStorage.size();
  }

  private void cleanBelow(final int value) {
    int countBeforeHalf = cacheStorage.size() / 2;
    for (final Map.Entry<CacheKey, Map<CacheKey, Future<Optional<V>>>> cachedElement : cacheStorage
            .entrySet()) {
      if (cachedElement.getKey().getNumberOfUses() < value) {
        cacheStorage.remove(cachedElement.getKey());
        countBeforeHalf--;
      }
      if (countBeforeHalf == 0) {
        break;
      }
    }
  }

  private int defineMedian() {
    int totalSum = 0;
    for (final Map.Entry<CacheKey, Map<CacheKey, Future<Optional<V>>>> cachedElement : cacheStorage
            .entrySet()) {
      totalSum += cachedElement.getKey().getNumberOfUses();
    }
    return totalSum / cacheStorage.size();
  }

  private int countUnderused() {
    int underusedNumber = 0;
    for (final Map.Entry<CacheKey, Map<CacheKey, Future<Optional<V>>>> cachedElement : cacheStorage
            .entrySet()) {
      if (cachedElement.getKey().getNumberOfUses() < underused) {
        underusedNumber++;
      }
    }
    return underusedNumber;
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
