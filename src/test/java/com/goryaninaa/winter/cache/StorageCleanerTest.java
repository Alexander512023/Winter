package com.goryaninaa.winter.cache;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
class StorageCleanerTest {

    private static Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage1;
    private static Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage2;
    private static Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage3;
    private static StorageCleaner<PersonC> storageCleaner1;
    private static StorageCleaner<PersonC> storageCleaner2;
    private static StorageCleaner<PersonC> storageCleaner3;



    @BeforeAll
    static void init() {
        Properties properties = new Properties();
        properties.put("Cache.size", "3");
        cacheStorage1 = new ConcurrentHashMap<>();
        storageCleaner1 = new StorageCleaner<>(cacheStorage1, properties);
        cacheStorage2 = new ConcurrentHashMap<>();
        storageCleaner2 = new StorageCleaner<>(cacheStorage2, properties);
        cacheStorage3 = new ConcurrentHashMap<>();
        storageCleaner3 = new StorageCleaner<>(cacheStorage3, properties);
    }
    @Test
    void storageCleanerShouldMaintainCacheSize() throws InterruptedException {
        StorageCleanerScenarioPreparator preparator =
                new StorageCleanerScenarioPreparator("11123", cacheStorage1);
        preparator.doWork();
        storageCleaner1.run();
        Thread.sleep(25);
        storageCleaner1.shutdown();
        assertEquals(3, cacheStorage1.size());
    }

    @Test
    void storageCleanerShouldKeepHighlyDesiredData() throws InterruptedException {
        StorageCleanerScenarioPreparator preparator =
                new StorageCleanerScenarioPreparator("11123", cacheStorage2);
        preparator.doWork();
        storageCleaner2.run();
        Thread.sleep(25);
        storageCleaner2.shutdown();
        assertTrue(isConfirmed(cacheStorage2));
    }

    @Test
    void storageCleanerShouldNotWorkAfterShutdown() throws InterruptedException {
        StorageCleanerScenarioPreparator preparator =
                new StorageCleanerScenarioPreparator("11123", cacheStorage3);
        preparator.doWork();
        storageCleaner3.run();
        Thread.sleep(25);
        storageCleaner3.shutdown();
        Thread.sleep(50);
        preparator.doWork();
        assertEquals(5, cacheStorage3.size());
    }

    private boolean isConfirmed(Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage) {
        CacheKeyFactory keyFactory = StorageTestEnvGenerator.personIdCacheKeyFactory;
        CacheKey person4CacheKey = keyFactory.generateCacheKey(4, PersonAccessStrategyType.ID);
        CacheKey person5CacheKey = keyFactory.generateCacheKey(5, PersonAccessStrategyType.ID);
        return  cacheStorage.containsKey(person4CacheKey) && cacheStorage.containsKey(person5CacheKey);
    }
}