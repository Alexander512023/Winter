package com.goryaninaa.winter;

public class Waitility {
    public static void waitExecution(Object lock, int millis) throws InterruptedException {
        synchronized (lock) {
            lock.wait(millis);
        }
    }
}
