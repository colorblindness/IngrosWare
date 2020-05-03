package me.xenforu.kelo.manager.impl;

import me.xenforu.kelo.manager.IManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public abstract class MapManager<K, V> implements IManager {
    private Map<K, V> map = new HashMap<>();

    public void put(K k, V v) {
        map.put(k , v);
    }

    public void remove(K k) {
        map.remove(k);
    }

    public void clear() {
        map.clear();
    }

    public Collection<V> getValues() {
        return map.values();
    }

    public Collection<K> getKeySet() {
        return map.keySet();
    }

    public Set<Map.Entry<K, V>> getEntry() {
        return map.entrySet();
    }

    public void setMap(Map<K, V> map) {
        this.map = map;
    }

    public Map<K, V> getMap() {
        return map;
    }
}
