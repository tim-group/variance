package com.youdevise.variance;

import java.util.Set;

import com.google.common.base.Function;

public interface TypeConverterDictionary {
    <S, T> void register(Class<S> sourceClass, Class<T> targetClass, Function<S, T> converter);
    Set<Class<?>> sourceClasses();
    Set<Class<?>> targetClassesFor(Class<?> sourceClass);
    <S, T> Function<S, T> converterFor(Class<S> sourceClass, Class<T> targetClass);
}