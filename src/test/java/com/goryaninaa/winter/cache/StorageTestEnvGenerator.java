package com.goryaninaa.winter.cache;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class StorageTestEnvGenerator {

    protected static final CacheKeyFactory personIdCacheKeyFactory = generatePersonIdCacheKeyFactory();
    private final PersonDaoStub personDaoStub;
    private final Cache<PersonC> personCache;

    protected StorageTestEnvGenerator() {
        Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorageMap =
                new ConcurrentHashMap<>();
        this.personDaoStub = generatePersonDaoStub();
        this.personCache = generatePersonCache(this.personDaoStub, cacheStorageMap);
    }

    protected PersonDaoStub getPersonDaoStub() {
        return personDaoStub;
    }

    protected Cache<PersonC> getPersonCache() {
        return personCache;
    }

    private Cache<PersonC> generatePersonCache(
            PersonDaoStub personDaoStub,
            Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorageMap) {
        return new Storage<>(personDaoStub, cacheStorageMap);
    }

    private PersonDaoStub generatePersonDaoStub() {
        DataAccessStrategy<PersonC> personDataAccessStrategy = new PersonDataAccessByIdStrategy();
        Map<String, DataAccessStrategy<PersonC>> personDataAccessStrategies =
                new ConcurrentHashMap<>(
                        Map.of(personDataAccessStrategy.getStrategyType(), personDataAccessStrategy));
        return new PersonDaoStub(personDataAccessStrategies);
    }

    private static CacheKeyFactory generatePersonIdCacheKeyFactory() {
        KeyExtractStrategy personIdExtractStrategy = new PersonIdExtractStrategy();
        Map<String, KeyExtractStrategy> personIdExtractStrategies =
                new ConcurrentHashMap<>(
                        Map.of(personIdExtractStrategy.getStrategyType(), personIdExtractStrategy));
        return new CacheKeyFactory(personIdExtractStrategies);
    }
}
