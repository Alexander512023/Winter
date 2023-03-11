package com.goryaninaa.winter.cache;

import javax.naming.OperationNotSupportedException;
import java.util.Map;
import java.util.Optional;

public class PersonDataMediatorStub extends DataMediator<PersonC> {

    private int callCount;

    protected PersonDataMediatorStub(Map<String, DataAccessStrategy<PersonC>> dataAccesses) {
        super(dataAccesses);
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
