package me.xenforu.kelo.manager.impl;

import me.xenforu.kelo.manager.IManager;

import java.util.ArrayList;
import java.util.List;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public abstract class ListManager<K> implements IManager {
    private List<K> list = new ArrayList<>();

    public void add(K k) {
        list.add(k);
    }

    public void remove(K k) {
        list.remove(k);
    }

    public void clear() {
        list.clear();
    }

    public void setList(List<K> list) {
        this.list = list;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public List<K> getList() {
        return list;
    }
}
