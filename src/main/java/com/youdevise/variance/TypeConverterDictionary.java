package com.youdevise.variance;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import static com.google.common.collect.Maps.newHashMap;

public class TypeConverterDictionary {
    private final Map<Class<?>, Map<Class<?>, Function<?, ?>>> registry;
    
    public TypeConverterDictionary() {
        registry = Maps.newHashMap();
    }
    
    public TypeConverterDictionary(Map<Class<?>, Map<Class<?>, Function<?, ?>>> registry) {
        this.registry = registry;
    }
    
    public <S, T> void register(Class<S> sourceClass, Class<T> targetClass, Function<S, T> converter) {
        convertersFor(sourceClass).put(targetClass, converter);
    }
    
    private Map<Class<?>, Function<?, ?>> convertersFor(Class<?> sourceClass) {
        if (registry.containsKey(sourceClass)) {
            return registry.get(sourceClass);
        }
        Map<Class<?>, Function<?, ?>> converters = newHashMap();
        registry.put(sourceClass, converters);
        return converters;
    }
    
    public Set<Class<?>> sourceClasses() {
        return registry.keySet();
    }

    public Set<Class<?>> targetClassesFor(Class<?> sourceClass) {
        if (registry.containsKey(sourceClass)) {
            return registry.get(sourceClass).keySet();
        }
        return Collections.emptySet();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <S, T> Function<S, T> converterFor(Class<S> sourceClass, Class<T> targetClass) {
        if (!registry.containsKey(sourceClass)) {
            return null;
        }
        return (Function) registry.get(sourceClass).get(targetClass);
    }
}