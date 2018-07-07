package com.ilummc.tlib.scripting.util;

import java.util.Map;

public class Entries {

    public static <K, V> Map.Entry<K, V> of(K k, V v) {
        return new Map.Entry<K, V>() {
            @Override
            public K getKey() {
                return k;
            }

            @Override
            public V getValue() {
                return v;
            }

            @Override
            public V setValue(V value) {
                return v;
            }
        };
    }
}
