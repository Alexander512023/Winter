package com.goryaninaa.winter.cache;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
class StorageCleanerTest {

    @Test
    void storageCleanerShouldMaintainCacheSize() throws InterruptedException {
        final Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage =
                new ConcurrentHashMap<>();
        Properties properties = new Properties();
        properties.put("Cache.size", "3");
        StorageCleaner<PersonC> storageCleaner = new StorageCleaner<>(cacheStorage, properties);
        StorageCleanerScenarioPreparator preparator =
                new StorageCleanerScenarioPreparator("11123", cacheStorage);
        preparator.doWork();
        storageCleaner.run();
        Thread.sleep(25);
        storageCleaner.shutdown();
        assertEquals(3, cacheStorage.size());
    }

    @Test
    void storageCleanerShouldKeepHighlyDesiredData() throws InterruptedException {
        final Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage =
                new ConcurrentHashMap<>();
        Properties properties = new Properties();
        properties.put("Cache.size", "3");
        StorageCleaner<PersonC> storageCleaner = new StorageCleaner<>(cacheStorage, properties);
        StorageCleanerScenarioPreparator preparator =
                new StorageCleanerScenarioPreparator("11123", cacheStorage);
        preparator.doWork();
        storageCleaner.run();
        Thread.sleep(25);
        storageCleaner.shutdown();
        assertTrue(isConfirmed(cacheStorage));
    }

    @Test
    void storageCleanerShouldNotWorkAfterShutdown() throws InterruptedException {
        final Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage =
                new ConcurrentHashMap<>();
        Properties properties = new Properties();
        properties.put("Cache.size", "3");
        StorageCleaner<PersonC> storageCleaner = new StorageCleaner<>(cacheStorage, properties);
        StorageCleanerScenarioPreparator preparator =
                new StorageCleanerScenarioPreparator("11123", cacheStorage);
        preparator.doWork();
        storageCleaner.run();
        Thread.sleep(25);
        storageCleaner.shutdown();
        Thread.sleep(50);
        preparator.doWork();
        assertEquals(5, cacheStorage.size());
    }

    private boolean isConfirmed(Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage) {
        CacheKeyFactory keyFactory = StorageTestEnvGenerator.personIdCacheKeyFactory;
        CacheKey person4CacheKey = keyFactory.generateCacheKey(4, PersonAccessStrategyType.ID);
        CacheKey person5CacheKey = keyFactory.generateCacheKey(5, PersonAccessStrategyType.ID);
        return  cacheStorage.containsKey(person4CacheKey) && cacheStorage.containsKey(person5CacheKey);
    }
}