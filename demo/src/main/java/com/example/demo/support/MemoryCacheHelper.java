package com.example.demo.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class MemoryCacheHelper {
    private static final ConcurrentMap<String, Object> CACHE = new ConcurrentHashMap<>();
    
    public static void put(String key, Object value) {
        CACHE.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) CACHE.get(key);
    }
    
    public static void remove(String key) {
        CACHE.remove(key);
    }
}
