package com.goryaninaa.winter.cache;

import java.util.List;

public interface CacheKeyFactory {


    CacheKey generateCacheKey(final Object key, final String accessStrategy);

    /**
     * Use this method in case you should delete value from cache from all access strategies.
     *
     * @param entity - value-object, that you want to delete from cache
     * @return - list of all possible cache keys for passed object
     */
    @SuppressWarnings("unused")
    List<CacheKey> generateAllCacheKeys(final Object entity);
}
