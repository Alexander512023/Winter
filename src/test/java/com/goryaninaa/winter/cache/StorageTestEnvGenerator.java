package com.goryaninaa.winter.cache;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class StorageTestEnvGenerator {

    protected static final CacheKeyFactory personIdCacheKeyFactory = generatePersonIdCacheKeyFactory();
    private final PersonDataMediatorStub personDataMediatorStub;
    private final PersonDaoStub personDaoStub = new PersonDaoStub();
    private final Cache<PersonC> personCache;

    protected StorageTestEnvGenerator() {
        Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorageMap =
                new ConcurrentHashMap<>();
        this.personDataMediatorStub = generatePersonDataMediatorStub();
        this.personCache = generatePersonCache(this.personDataMediatorStub, cacheStorageMap);
    }

    protected PersonDataMediatorStub getPersonDataMediatorStub() {
        return personDataMediatorStub;
    }

    protected Cache<PersonC> getPersonCache() {
        return personCache;
    }

    protected PersonDaoStub getPersonDaoStub() {
        return personDaoStub;
    }

    private Cache<PersonC> generatePersonCache(
            PersonDataMediatorStub personDaoStub,
            Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorageMap) {
        return new Storage<>(personDaoStub, cacheStorageMap);
    }

    private PersonDataMediatorStub generatePersonDataMediatorStub() {
        DataAccessStrategy<PersonC> personDataAccessStrategy =
                new PersonDataAccessByIdStrategy(personDaoStub);
        Map<String, DataAccessStrategy<PersonC>> personDataAccessStrategies =
                new ConcurrentHashMap<>(
                        Map.of(personDataAccessStrategy.getStrategyType(), personDataAccessStrategy));
        return new PersonDataMediatorStub(personDataAccessStrategies);
    }

    private static CacheKeyFactory generatePersonIdCacheKeyFactory() {
        KeyExtractStrategy personIdExtractStrategy = new PersonIdExtractStrategy();
        Map<String, KeyExtractStrategy> personIdExtractStrategies =
                new ConcurrentHashMap<>(
                        Map.of(personIdExtractStrategy.getStrategyType(), personIdExtractStrategy));
        return new CacheKeyFactory(personIdExtractStrategies);
    }
}
