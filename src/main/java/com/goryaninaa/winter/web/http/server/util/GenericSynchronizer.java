package com.goryaninaa.winter.web.http.server.util;

import java.util.ArrayList;
import java.util.List;

public class GenericSynchronizer<V> implements Synchronizer<V> {

    private final List<Object> locks;
    private static final int AMOUNT = 1000;


    public GenericSynchronizer() {
        locks = defineLocks();
    }

    @Override
    public Object getLock(final V object) {
        int index = object.hashCode() & (AMOUNT - 1);
        return locks.get(index);
    }

    private List<Object> defineLocks() {
        List<Object> newLocks = new ArrayList<>();
        for (int i = 0; i < AMOUNT; i++) {
            newLocks.add(new Object());
        }
        return newLocks;
    }
}
