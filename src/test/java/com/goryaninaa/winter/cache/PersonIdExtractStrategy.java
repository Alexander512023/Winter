package com.goryaninaa.winter.cache;

public class PersonIdExtractStrategy implements KeyExtractStrategy {
    @Override
    public Object extractKey(Object entity) {
        return ((PersonC) entity).getId();
    }

    @Override
    public String getStrategyType() {
        return PersonAccessStrategyType.ID;
    }
}
