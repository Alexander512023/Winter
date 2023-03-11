package com.goryaninaa.winter.cache;

import java.util.Optional;

public class PersonDataAccessByIdStrategy implements DataAccessStrategy<PersonC> {

    private final PersonDaoStub dao;

    public PersonDataAccessByIdStrategy(PersonDaoStub dao) {
        this.dao = dao;
    }

    @Override
    public Optional<PersonC> getData(Object key) {
        return Optional.ofNullable(dao.getOneById((Integer)key));
    }

    @Override
    public String getStrategyType() {
        return PersonAccessStrategyType.ID;
    }
}
