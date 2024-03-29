package com.goryaninaa.winter.cache;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageTest {

    private static CacheKeyFactoryStandard cacheKeyFactory;
    private static PersonDaoStub personDaoStub1;
    private static Cache<PersonC> personCache1;
    private static PersonDataMediatorStub personDataMediatorStub1;
    private static PersonDaoStub personDaoStub2;
    private static Cache<PersonC> personCache2;
    private static PersonDataMediatorStub personDataMediatorStub2;
    private static PersonDaoStub personDaoStub3;
    private static Cache<PersonC> personCache3;
    private static PersonDataMediatorStub personDataMediatorStub3;
    private static PersonDaoStub personDaoStub4;
    private static Cache<PersonC> personCache4;
    private static PersonDataMediatorStub personDataMediatorStub4;

    @BeforeAll
    public static void init() {
        createTestEnv();
    }

    @Test
    void getDataShouldReturnFromDaoIfNotInCache() {
        PersonC person1 = new PersonC(1);
        PersonC person2 = new PersonC(2);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub1.save(person1);
        personDaoStub1.save(person2);
        PersonC personFromDb = personCache1.getData(person1Key).orElseThrow();
        assertTrue(personDataMediatorStub1.getCallCount() == 1 && person1.equals(personFromDb));
    }

    @Test
    void getDataShouldReturnFromCacheIfPresent() {
        PersonC person1 = new PersonC(1);
        PersonC person2 = new PersonC(2);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub2.save(person1);
        personDaoStub2.save(person2);
        PersonC personFromDb = personCache2.getData(person1Key).orElseThrow();
        System.out.println(personFromDb); // NOPMD
        PersonC personFromCache = personCache2.getData(person1Key).orElseThrow();
        assertTrue(personDataMediatorStub2.getCallCount() == 1 && person1.equals(personFromCache));
    }

    @Test
    void getDataShouldHandleConcurrentSituation() throws ExecutionException, InterruptedException {
        PersonC person1 = new PersonC(1);
        PersonC person2 = new PersonC(2);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub3.save(person1);
        personDaoStub3.save(person2);
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
        assertTrue(personDataMediatorStub3.getCallCount() == 1 && check);
    }

    @Test
    void removeShouldRemoveDataFromCache() {
        PersonC person1 = new PersonC(1);
        PersonC person2 = new PersonC(2);
        CacheKey person1Key =
                cacheKeyFactory.generateCacheKey(person1.getId(), PersonAccessStrategyType.ID);
        personDaoStub4.save(person1);
        personDaoStub4.save(person2);
        PersonC personFromDb = personCache4.getData(person1Key).orElseThrow();
        System.out.println(personFromDb); // NOPMD
        List<CacheKey> cacheKeys = new ArrayList<>(List.of(person1Key));
        personCache4.remove(cacheKeys);
        personFromDb = personCache4.getData(person1Key).orElseThrow();
        System.out.println(personFromDb); // NOPMD
        assertEquals(2, personDataMediatorStub4.getCallCount());
    }

    private Future<PersonC> getDataFromCacheTroughLatch(
            ExecutorService executorService, CountDownLatch latch, CacheKey key) {
        latch.countDown();
        return executorService.submit(() -> personCache3.getData(key).orElseThrow());
    }
    private static void createTestEnv() {
        StorageTestEnvGenerator generator1 = new StorageTestEnvGenerator();
        StorageTestEnvGenerator generator2 = new StorageTestEnvGenerator();
        StorageTestEnvGenerator generator3 = new StorageTestEnvGenerator();
        StorageTestEnvGenerator generator4 = new StorageTestEnvGenerator();
        cacheKeyFactory = StorageTestEnvGenerator.personIdCacheKeyFactory;
        personDaoStub1 = generator1.getPersonDaoStub();
        personCache1 = generator1.getPersonCache();
        personDataMediatorStub1 = generator1.getPersonDataMediatorStub();
        personDaoStub2 = generator2.getPersonDaoStub();
        personCache2 = generator2.getPersonCache();
        personDataMediatorStub2 = generator2.getPersonDataMediatorStub();
        personDaoStub3 = generator3.getPersonDaoStub();
        personCache3 = generator3.getPersonCache();
        personDataMediatorStub3 = generator3.getPersonDataMediatorStub();
        personDaoStub4 = generator4.getPersonDaoStub();
        personCache4 = generator4.getPersonCache();
        personDataMediatorStub4 = generator4.getPersonDataMediatorStub();
    }
}