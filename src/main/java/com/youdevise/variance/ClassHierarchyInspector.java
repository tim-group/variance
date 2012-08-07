package com.youdevise.variance;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newLinkedList;

public class ClassHierarchyInspector {
    
    private final Iterable<Class<?>> classes;
    
    public ClassHierarchyInspector(Iterable<Class<?>> classes) {
        this.classes = classes;
    }
    
    public <S> Class<? super S> nearestClassAssignableFrom(Class<S> klass) {
        if (Iterables.contains(classes,  klass)) {
            return klass;
        }
        return nearestSuperclassOf(klass);
    }

    public <S> Class<? super S> nearestSuperclassOf(Class<S> klass) {
        Collection<Class<? super S>> minima = nearestSuperclassesOf(klass);
        
        if (minima.size() > 1) {
            throw new IllegalArgumentException(String.format("Cannot find unambiguous nearest superclass of [%s] from the set [%s]",
                                                             klass,
                                                             Joiner.on(", ").join(minima)));
        }
        
        return Iterables.getFirst(minima, null);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <S> Collection<Class<? super S>> nearestSuperclassesOf(Class<S> klass) {
        Iterable<Class<? super S>> superclasses = (Iterable) filter(classes, isSuperclassOf(klass));
        
        Collection<Class<? super S>> minima = newLinkedList();
        for (Class<? super S> superclass : superclasses) {
            Iterables.removeIf(minima, isSuperclassOf(superclass));
            if (!Iterables.any(minima, isSubclassOf(superclass))) {
                minima.add(superclass);
            }
        }
        
        return minima;
    }
    
    public <T> Class<? extends T> nearestClassAssignableTo(Class<T> klass) {
        if (Iterables.contains(classes,  klass)) {
            return klass;
        }
        return nearestSubclassOf(klass);
    }
    
    public <T> Class<? extends T> nearestSubclassOf(Class<T> klass) {
        Collection<Class<? extends T>> maxima = nearestSubclassesOf(klass);
        
        if (maxima.size() > 1) {
            throw new IllegalArgumentException(String.format("Cannot find unambiguous nearest subclass of [%s] from the set [%s]",
                                                             klass,
                                                             Joiner.on(", ").join(maxima)));
        }
        return Iterables.getFirst(maxima, null);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> Collection<Class<? extends T>> nearestSubclassesOf(Class<T> klass) {
        Iterable<Class<? extends T>> subclasses = (Iterable) filter(classes, isSubclassOf(klass));
        
        Collection<Class<? extends T>> maxima = newLinkedList();
        for (Class<? extends T> subclass : subclasses) {
            Iterables.removeIf(maxima, isSubclassOf(subclass));
            if (!Iterables.any(maxima, isSuperclassOf(subclass))) {
                maxima.add(subclass);
            }
        }
        
        return maxima;
    }
    
    private <S> Predicate<Class<?>> isSuperclassOf(final Class<?> klass) {
        return new Predicate<Class<?>>() {
            @Override public boolean apply(Class<?> otherClass) {
                return !otherClass.equals(klass) && otherClass.isAssignableFrom(klass);
            }
        };
    }
    
    private <S> Predicate<Class<?>> isSubclassOf(final Class<?> klass) {
        return new Predicate<Class<?>>() {
            @Override public boolean apply(Class<?> otherClass) {
                return !otherClass.equals(klass) && klass.isAssignableFrom(otherClass);
            }
        };
    }
}