package com.example.warmest.api;

public interface WarmestDataStructureInterface {
    Integer put(String key, int value);

    Integer remove(String key);

    Integer get(String key);

    String getWarmest();
}
