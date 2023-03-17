package com.goryaninaa.winter.web.http.server.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenericSynchronizerTest {

    @Test
    void getLockShouldReturnSameLocksForObject() {
        Synchronizer<Object> synchronizer = new GenericSynchronizer<>();
        Object requestForLock = new Object();
        Object lock1 = synchronizer.getLock(requestForLock);
        Object lock2 = synchronizer.getLock(requestForLock);
        assertEquals(lock1, lock2);
    }

    @Test
    void getLockShouldReturnDifferentLocksForObjects() {
        Synchronizer<Object> synchronizer = new GenericSynchronizer<>();
        Object requestForLock1 = new Object();
        Object requestForLock2 = new Object();
        Object lock1 = synchronizer.getLock(requestForLock1);
        Object lock2 = synchronizer.getLock(requestForLock2);
        assertNotEquals(lock1, lock2);
    }
}