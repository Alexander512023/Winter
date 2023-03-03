package com.goryaninaa.winter.cache;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class StorageTest {

    private static CacheKeyFactory cacheKeyFactory;
    private static PersonDaoStub personDaoStub;
    private static Cache<PersonC> personCache;

    @Test
    void getDataShouldReturnFromDaoIfNotInCache() {
        PersonC person1 = new PersonC();
        person1.setId(1);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub.save(person1);
        PersonC personFromDb = personCache.getData(person1Key).get();
        assertTrue(personDaoStub.getCallCount() == 1 && person1.equals(personFromDb));
    }

    @Test
    void getDataShouldReturnFromCacheIfPresent() {
        PersonC person1 = new PersonC();
        person1.setId(1);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub.save(person1);
        PersonC personFromDb = personCache.getData(person1Key).get();
        System.out.println(personFromDb);
        PersonC personFromCache = personCache.getData(person1Key).get();
        assertTrue(personDaoStub.getCallCount() == 1 && person1.equals(personFromCache));
    }

//    TODO add concurrent calls to cache via latch
//    TODO add tests on Update
//    TODO add tests on Delete
    @Test
    void remove() {
    }
    
    @BeforeAll
    public static void init() {
        createTestEnv();
    }

    private static void createTestEnv() {
        cacheKeyFactory = generateFactory();
        personDaoStub = generateDaoStub();
        personCache = generateCache(personDaoStub);
    }

    private static Cache<PersonC> generateCache(PersonDaoStub personDaoStub) {
        Properties properties = new Properties();
        properties.setProperty("Cache.size", "1");
        properties.setProperty("Cache.underused", "2");
        return new Storage<>(personDaoStub, properties);
    }

    private static PersonDaoStub generateDaoStub() {
        DataAccessStrategy<PersonC> personDataAccessStrategy = new PersonDataAccessByIdStrategy();
        Map<String, DataAccessStrategy<PersonC>> personDataAccessStrategies =
                new ConcurrentHashMap<>(
                        Map.of(personDataAccessStrategy.getStrategyType(), personDataAccessStrategy));
        return new PersonDaoStub(personDataAccessStrategies);
    }

    private static CacheKeyFactory generateFactory() {
        KeyExtractStrategy personIdExtractStrategy = new PersonIdExtractStrategy();
        Map<String, KeyExtractStrategy> personIdExtractStrategies =
                new ConcurrentHashMap<>(
                        Map.of(personIdExtractStrategy.getStrategyType(), personIdExtractStrategy));
        return new CacheKeyFactory(personIdExtractStrategies);
    }
}