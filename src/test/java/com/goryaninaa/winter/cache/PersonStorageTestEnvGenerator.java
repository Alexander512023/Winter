package com.goryaninaa.winter.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersonStorageTestEnvGenerator {

    protected static final CacheKeyFactory personIdCacheKeyFactory = generatePersonIdCacheKeyFactory();
    private final PersonDaoStub personDaoStub;
    private final Cache<PersonC> personCache;

    protected PersonStorageTestEnvGenerator() {
        this.personDaoStub = generatePersonDaoStub();
        this.personCache = generatePersonCache(this.personDaoStub);
    }

    protected PersonDaoStub getPersonDaoStub() {
        return personDaoStub;
    }

    protected Cache<PersonC> getPersonCache() {
        return personCache;
    }

    private Cache<PersonC> generatePersonCache(PersonDaoStub personDaoStub) {
        return new Storage<>(personDaoStub);
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
