package com.goryaninaa.winter.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.naming.OperationNotSupportedException;

/**
 * You should extend this class if you want to use cache before DB.
 *
 * @author Alex Goryanin
 * @param <V> - generic of type you work with using this DAO
 */
public class DataAccessObject<V> {

  private final Map<String, DataAccessStrategy> dataAccesses;
  private final List<V> dataList = new ArrayList<>();

  protected DataAccessObject(final Map<String, DataAccessStrategy> dataAccesses) {
    this.dataAccesses = dataAccesses;
  }

  /* package */ Optional<V> getData(final Object key, final String accessType)
      throws OperationNotSupportedException {
    if (!dataAccesses.containsKey(accessType)) {
      throw new OperationNotSupportedException("Not supported");
    }
    final DataAccessStrategy dataAccess = dataAccesses.get(accessType);
    return dataAccess.getData(key, dataList);
  }

  @SuppressWarnings("unused")
  public List<V> getDataList() {
    return dataList;
  }
}
