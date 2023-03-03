package com.goryaninaa.winter.cache;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class StorageTest {

    private static CacheKeyFactory cacheKeyFactory;
    private static PersonDaoStub personDaoStub1;
    private static Cache<PersonC> personCache1;
    private static PersonDaoStub personDaoStub2;
    private static Cache<PersonC> personCache2;
    private static PersonDaoStub personDaoStub3;
    private static Cache<PersonC> personCache3;
    private static PersonDaoStub personDaoStub4;
    private static Cache<PersonC> personCache4;

    @BeforeAll
    public static void init() {
        createTestEnv();
    }

    @Test
    void getDataShouldReturnFromDaoIfNotInCache() {
        PersonC person1 = new PersonC();
        person1.setId(1);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub1.save(person1);
        PersonC personFromDb = personCache1.getData(person1Key).get();
        assertTrue(personDaoStub1.getCallCount() == 1 && person1.equals(personFromDb));
    }

    @Test
    void getDataShouldReturnFromCacheIfPresent() {
        PersonC person1 = new PersonC();
        person1.setId(1);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub2.save(person1);
        PersonC personFromDb = personCache2.getData(person1Key).get();
        System.out.println(personFromDb);
        PersonC personFromCache = personCache2.getData(person1Key).get();
        assertTrue(personDaoStub2.getCallCount() == 1 && person1.equals(personFromCache));
    }

    @Test
    void getDataShouldHandleConcurrentSituation() throws ExecutionException, InterruptedException {
        PersonC person1 = new PersonC();
        person1.setId(1);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub3.save(person1);
        List<Future<PersonC>> personFuturesList = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            personFuturesList.add(getDataFromCacheTroughLatch(executorService, latch, person1Key));
        }
        boolean check = true;
        for (Future<PersonC> personCFuture : personFuturesList) {
            check = check && person1.equals(personCFuture.get());
        }
        assertTrue(personDaoStub3.getCallCount() == 1 && check);
    }
    @Test
    void removeShouldRemoveDataFromCache() {
        PersonC person1 = new PersonC();
        person1.setId(1);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub4.save(person1);
        PersonC personFromDb = personCache4.getData(person1Key).get();
        System.out.println(personFromDb);
        List<CacheKey> cacheKeys = new ArrayList<>(List.of(person1Key));
        personCache4.remove(cacheKeys);
        personFromDb = personCache4.getData(person1Key).get();
        System.out.println(personFromDb);
        assertEquals(2, personDaoStub4.getCallCount());
    }

    private Future<PersonC> getDataFromCacheTroughLatch(
            ExecutorService executorService, CountDownLatch latch, CacheKey key) {
        latch.countDown();
        return executorService.submit(() -> personCache3.getData(key).get());
    }
    private static void createTestEnv() {
        cacheKeyFactory = generateFactory();
        personDaoStub1 = generateDaoStub();
        personCache1 = generateCache(personDaoStub1);
        personDaoStub2 = generateDaoStub();
        personCache2 = generateCache(personDaoStub2);
        personDaoStub3 = generateDaoStub();
        personCache3 = generateCache(personDaoStub3);
        personDaoStub4 = generateDaoStub();
        personCache4 = generateCache(personDaoStub4);
    }

    private static Cache<PersonC> generateCache(PersonDaoStub personDaoStub) {
        return new Storage<>(personDaoStub);
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