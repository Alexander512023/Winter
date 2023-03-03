package com.goryaninaa.winter.cache;

import javax.naming.OperationNotSupportedException;
import java.util.Map;
import java.util.Optional;

public class PersonDaoStub extends DataAccessObject<PersonC> {

    private int callCount;
    public PersonDaoStub(Map<String, DataAccessStrategy<PersonC>> dataAccesses) {
        super(dataAccesses);
    }

    public void save(PersonC person) {
        super.getDataList().add(person);
    }

    public int getCallCount() {
        return callCount;
    }

    @Override
    protected Optional<PersonC> getData(final Object key, final String accessType)
            throws OperationNotSupportedException {
        callCount++;
        return super.getData(key, accessType);
    }
}
