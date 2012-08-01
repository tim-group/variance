package com.youdevise.variance;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

public class UncachedTypeConverterRegistry implements TypeConverterRegistry {

    private final Map<Class<?>, Map<Class<?>, Function<?, ?>>> registry;
    
    public UncachedTypeConverterRegistry() {
        registry = Maps.newHashMap();
    }
    
    public UncachedTypeConverterRegistry(Map<Class<?>, Map<Class<?>, Function<?, ?>>> wrappedMap) {
        registry = wrappedMap;
    }
    
    public <S, T> UncachedTypeConverterRegistry register(Class<S> sourceClass, Class<T> targetClass, Function<S, T> converter) {
        convertersFor(sourceClass).put(targetClass, converter);
        return this;
    }
    
    private Map<Class<?>, Function<?, ?>> convertersFor(Class<?> sourceClass) {
        if (registry.containsKey(sourceClass)) {
            return registry.get(sourceClass);
        }
        Map<Class<?>, Function<?, ?>> converters = newHashMap();
        registry.put(sourceClass, converters);
        return converters;
    }
    
    @Override
    public boolean hasConverter(Class<?> sourceClass, Class<?> targetClass) {
        return findConverter(sourceClass, targetClass) != null;
    }

    @Override
    public <S, T> Function<? super S, ? extends T> getConverter(Class<S> sourceClass, Class<T> targetClass) {
        Function<? super S, ? extends T> converter = findConverter(sourceClass, targetClass);
        Preconditions.checkNotNull(converter, "No converter found between [%s] and [%s]", sourceClass, targetClass);
        return converter;
    }
    
    private <S, T> Function<? super S, ? extends T> findConverter(Class<S> sourceClass, Class<T> targetClass) {
        Class<? super S> nearestSuperclass = nearestSuperclassWithMatchersFor(registry.keySet(), sourceClass);
        while (nearestSuperclass != null) {
            Map<Class<?>, Function<?, ?>> converters = registry.get(nearestSuperclass);
            Function<? super S, ? extends T> converter = findConverterFor(targetClass, converters);
            if (converter != null) {
                return converter;
            }
            nearestSuperclass = nearestSuperclassWithMatchersFor(registry.keySet(), nearestSuperclass);
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked" })
    private <S> Class<? super S> nearestSuperclassWithMatchersFor(Iterable<Class<?>> classes, Class<S> sourceClass) {
        Iterable<Class<?>> superclasses = filter(classes, isSuperclassOf(sourceClass));
        
        Collection<Class<?>> minima = newLinkedList();
        for (Class<?> superclass : superclasses) {
            Iterables.removeIf(minima, isSuperclassOf(superclass));
            if (!Iterables.any(minima, isSubclassOf(superclass))) {
                minima.add(superclass);
            }
        }
        
        if (minima.size() > 1) {
            throw new IllegalArgumentException(String.format("Cannot find a single least superclass of [%s] from the set [%s]",
                                                             sourceClass,
                                                             Joiner.on(", ").join(minima)));
        }
        
        return (Class<? super S>) Iterables.getFirst(minima, null);
    }
    
    @SuppressWarnings({ "unchecked" })
    private <T> Class<? extends T> nearestSubclassWithMatcherFor(Iterable<Class<?>> classes, Class<T> targetClass) {
        Iterable<Class<?>> subclasses = filter(classes, isSubclassOf(targetClass));
        
        Collection<Class<?>> maxima = newLinkedList();
        for (Class<?> subclass : subclasses) {
            Iterables.removeIf(maxima, isSubclassOf(subclass));
            if (!Iterables.any(maxima, isSuperclassOf(subclass))) {
                maxima.add(subclass);
            }
        }
        
        if (maxima.size() > 1) {
            throw new IllegalArgumentException(String.format("Cannot find a single greatest subclass of [%s] from the set [%s]",
                                                             targetClass,
                                                             Joiner.on(", ").join(maxima)));
        }
        return (Class<? extends T>) Iterables.getFirst(maxima, null);
    }
    
    private <S> Predicate<Class<?>> isSuperclassOf(final Class<?> sourceClass) {
        return new Predicate<Class<?>>() {
            @Override public boolean apply(Class<?> superClass) {
                return superClass.isAssignableFrom(sourceClass);
            }
        };
    }
    
    private <S> Predicate<Class<?>> isSubclassOf(final Class<?> targetClass) {
        return new Predicate<Class<?>>() {
            @Override public boolean apply(Class<?> subClass) {
                return targetClass.isAssignableFrom(subClass);
            }
        };
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <S, T> Function<? super S, ? extends T> findConverterFor(Class<T> targetClass, Map<Class<?>, Function<?, ?>> converters) {
        Class<? extends T> targetSubclass = nearestSubclassWithMatcherFor(converters.keySet(), targetClass);
        if (targetSubclass == null) {
            return null;            
        }
        return (Function) converters.get(targetSubclass);
    }

}
