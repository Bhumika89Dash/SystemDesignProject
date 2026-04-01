package com.SystemDesign.Project.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true); // access-order = true (LRU)
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean shouldRemove = size() > capacity;

        if (shouldRemove) {
            System.out.println("Removing LRU entry: " + eldest.getKey());
        }

        return shouldRemove;
    }
}
