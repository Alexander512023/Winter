package com.goryaninaa.winter.cache;

import com.goryaninaa.winter.Waitility;
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
    private static StorageCleanerScenarioPreparator preparator1;
    private static StorageCleanerScenarioPreparator preparator2;
    private static StorageCleanerScenarioPreparator preparator3;

    @BeforeAll
    static void init() {
        Properties properties = new Properties();
        properties.put("Winter.Cache.size", "3");
        final String SCENARIO = "11123";
        cacheStorage1 = new ConcurrentHashMap<>();
        storageCleaner1 = new StorageCleaner<>(cacheStorage1, properties);
        preparator1 = new StorageCleanerScenarioPreparator(SCENARIO, cacheStorage1);
        cacheStorage2 = new ConcurrentHashMap<>();
        storageCleaner2 = new StorageCleaner<>(cacheStorage2, properties);
        preparator2 = new StorageCleanerScenarioPreparator(SCENARIO, cacheStorage2);
        cacheStorage3 = new ConcurrentHashMap<>();
        storageCleaner3 = new StorageCleaner<>(cacheStorage3, properties);
        preparator3 = new StorageCleanerScenarioPreparator(SCENARIO, cacheStorage3);
    }

    @Test
    void storageCleanerShouldMaintainCacheSize() throws InterruptedException {
        preparator1.doWork();
        storageCleaner1.run();
        Waitility.waitExecution(this, 50);
        storageCleaner1.shutdown();
        assertEquals(3, cacheStorage1.size());
    }

    @Test
    void storageCleanerShouldKeepHighlyDesiredData() throws InterruptedException {
        preparator2.doWork();
        storageCleaner2.run();
        Waitility.waitExecution(this, 50);
        storageCleaner2.shutdown();
        assertTrue(isConfirmed(cacheStorage2));
    }

    @Test
    void storageCleanerShouldNotWorkAfterShutdown() throws InterruptedException {
        preparator3.doWork();
        storageCleaner3.run();
        Waitility.waitExecution(this, 50);
        storageCleaner3.shutdown();
        Waitility.waitExecution(this, 50);
        preparator3.doWork();
        Waitility.waitExecution(this, 50);
        assertEquals(5, cacheStorage3.size());
    }

    private boolean isConfirmed(Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage) {
        CacheKeyFactory keyFactory = StorageTestEnvGenerator.personIdCacheKeyFactory;
        CacheKey person4CacheKey = keyFactory.generateCacheKey(4, PersonAccessStrategyType.ID);
        CacheKey person5CacheKey = keyFactory.generateCacheKey(5, PersonAccessStrategyType.ID);
        return  cacheStorage.containsKey(person4CacheKey) && cacheStorage.containsKey(person5CacheKey);
    }
}