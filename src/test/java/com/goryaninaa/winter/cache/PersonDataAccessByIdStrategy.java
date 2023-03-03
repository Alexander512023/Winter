package com.goryaninaa.winter.cache;

import java.util.List;
import java.util.Optional;

public class PersonDataAccessByIdStrategy implements DataAccessStrategy<PersonC> {
    @Override
    public Optional<PersonC> getData(Object key, List<PersonC> data) {
        Optional<PersonC> person = Optional.empty();
        for (final PersonC personFromList : data) {
            if (key.equals(personFromList.getId())) {
                person = Optional.of(personFromList);
                break;
            }
        }
        return person;
    }

    @Override
    public String getStrategyType() {
        return PersonAccessStrategyType.ID;
    }
}
