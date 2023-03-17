package com.goryaninaa.winter.web.http.server.util;

@SuppressWarnings("unused")
public interface Synchronizer<V> {

    Object getLock(V object);
}
