package com.goryaninaa.winter.cache;

import java.util.ArrayList;
import java.util.List;

public class PersonDaoStub {

    private final List<PersonC> personCList = new ArrayList<>();

    public void save(PersonC personC) {
        personCList.add(personC);
    }

    public PersonC getOneById(int id) {
        return personCList.stream().filter(personC -> personC.getId() == id).findFirst().orElseThrow();
    }
}
