package com.youdevise.variance;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

public class VariantMap implements Map<Variant, Variant> {

    private final Map<Variant, Variant> innerMap = Maps.newHashMap();
    
    @Override
    public int size() {
        return innerMap.size();
    }

    @Override
    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return innerMap.containsKey(Variant.of(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return innerMap.containsValue(Variant.of(value));
    }

    @Override
    public Variant get(Object key) {
        return innerMap.get(Variant.of(key));
    }

    @Override
    public Variant put(Variant key, Variant value) {
        return innerMap.put(key, value);
    }
    
    public Variant put(Object key, Object value) {
        return innerMap.put(Variant.of(key), Variant.of(value));
    }

    @Override
    public Variant remove(Object key) {
        return innerMap.remove(Variant.of(key));
    }

    @Override
    public void putAll(Map<? extends Variant, ? extends Variant> m) {
        innerMap.putAll(m);
    }

    @Override
    public void clear() {
        innerMap.clear();
    }

    @Override
    public Set<Variant> keySet() {
        return innerMap.keySet();
    }

    @Override
    public Collection<Variant> values() {
        return innerMap.values();
    }

    @Override
    public Set<java.util.Map.Entry<Variant, Variant>> entrySet() {
        return innerMap.entrySet();
    }

}
