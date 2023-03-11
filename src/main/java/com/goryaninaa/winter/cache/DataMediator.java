package com.goryaninaa.winter.cache;

import java.util.Map;
import java.util.Optional;
import javax.naming.OperationNotSupportedException;

/**
 * You should extend this class if you want to use cache before DB.
 *
 * @author Alex Goryanin
 * @param <V> - generic of type you work with using this DAO
 */
public class DataMediator<V> {

  private final Map<String, DataAccessStrategy<V>> dataAccesses;

  protected DataMediator(final Map<String, DataAccessStrategy<V>> dataAccesses) {
    this.dataAccesses = dataAccesses;
  }

  protected Optional<V> getData(final Object key, final String accessType)
      throws OperationNotSupportedException {
    if (!dataAccesses.containsKey(accessType)) {
      throw new OperationNotSupportedException("Not supported");
    }
    final DataAccessStrategy<V> dataAccess = dataAccesses.get(accessType);
    return dataAccess.getData(key);
  }

}
