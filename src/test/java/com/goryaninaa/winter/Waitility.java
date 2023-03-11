package com.goryaninaa.winter;

@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class Waitility {
    public static void waitExecution(final Object lock, final int millis)
            throws InterruptedException {
        synchronized (lock) {
            lock.wait(millis);
        }
    }
}
