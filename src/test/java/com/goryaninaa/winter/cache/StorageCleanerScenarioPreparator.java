package com.goryaninaa.winter.cache;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings({"OptionalGetWithoutIsPresent", "SameParameterValue"})
public class StorageCleanerScenarioPreparator {

    private final Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage;
    private final String scenarioInput;

    protected StorageCleanerScenarioPreparator(
            String scenarioInput,
            Map<CacheKey, Map<CacheKey, Future<Optional<PersonC>>>> cacheStorage) {
        this.scenarioInput = scenarioInput;
        this.cacheStorage = cacheStorage;
    }

    protected void doWork(){
        fillCache();
        applyScenario(scenarioInput);
    }

    private void applyScenario(String scenarioInput) {
        CacheKeyFactory keyFactory = StorageTestEnvGenerator.personIdCacheKeyFactory;
        CacheKey person1CacheKey = keyFactory.generateCacheKey(1, PersonAccessStrategyType.ID);
        CacheKey person2CacheKey = keyFactory.generateCacheKey(2, PersonAccessStrategyType.ID);
        CacheKey person3CacheKey = keyFactory.generateCacheKey(3, PersonAccessStrategyType.ID);
        CacheKey person4CacheKey = keyFactory.generateCacheKey(4, PersonAccessStrategyType.ID);
        CacheKey person5CacheKey = keyFactory.generateCacheKey(5, PersonAccessStrategyType.ID);
        for (int i = 0; i < Integer.parseInt(scenarioInput.substring(0, 1)); i++) {
            cacheStorage.get(person1CacheKey).keySet().stream().findFirst().get().use();
        }
        for (int i = 0; i < Integer.parseInt(scenarioInput.substring(1, 2)); i++) {
            cacheStorage.get(person2CacheKey).keySet().stream().findFirst().get().use();
        }
        for (int i = 0; i < Integer.parseInt(scenarioInput.substring(2, 3)); i++) {
            cacheStorage.get(person3CacheKey).keySet().stream().findFirst().get().use();
        }
        for (int i = 0; i < Integer.parseInt(scenarioInput.substring(3, 4)); i++) {
            cacheStorage.get(person4CacheKey).keySet().stream().findFirst().get().use();
        }
        for (int i = 0; i < Integer.parseInt(scenarioInput.substring(4, 5)); i++) {
            cacheStorage.get(person5CacheKey).keySet().stream().findFirst().get().use();
        }
    }

    private void fillCache() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Optional<PersonC>> futurePerson1 =
                executorService.submit(() -> Optional.of(new PersonC(1)));
        Future<Optional<PersonC>> futurePerson2 =
                executorService.submit(() -> Optional.of(new PersonC(2)));
        Future<Optional<PersonC>> futurePerson3 =
                executorService.submit(() -> Optional.of(new PersonC(3)));
        Future<Optional<PersonC>> futurePerson4 =
                executorService.submit(() -> Optional.of(new PersonC(4)));
        Future<Optional<PersonC>> futurePerson5 =
                executorService.submit(() -> Optional.of(new PersonC(5)));
        CacheKeyFactory cacheKeyFactory = StorageTestEnvGenerator.personIdCacheKeyFactory;
        CacheKey person1CacheKey = cacheKeyFactory.generateCacheKey(1, PersonAccessStrategyType.ID);
        CacheKey person2CacheKey = cacheKeyFactory.generateCacheKey(2, PersonAccessStrategyType.ID);
        CacheKey person3CacheKey = cacheKeyFactory.generateCacheKey(3, PersonAccessStrategyType.ID);
        CacheKey person4CacheKey = cacheKeyFactory.generateCacheKey(4, PersonAccessStrategyType.ID);
        CacheKey person5CacheKey = cacheKeyFactory.generateCacheKey(5, PersonAccessStrategyType.ID);
        cacheStorage.put(person1CacheKey, new ConcurrentHashMap<>(Map.of(person1CacheKey, futurePerson1)));
        cacheStorage.put(person2CacheKey, new ConcurrentHashMap<>(Map.of(person2CacheKey, futurePerson2)));
        cacheStorage.put(person3CacheKey, new ConcurrentHashMap<>(Map.of(person3CacheKey, futurePerson3)));
        cacheStorage.put(person4CacheKey, new ConcurrentHashMap<>(Map.of(person4CacheKey, futurePerson4)));
        cacheStorage.put(person5CacheKey, new ConcurrentHashMap<>(Map.of(person5CacheKey, futurePerson5)));
    }
}
