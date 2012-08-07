package com.youdevise.variance;

import com.google.common.base.Function;

public class TypeConverterRegistryBuilder {

    private final TypeConverterDictionary dictionary;
    
    public TypeConverterRegistryBuilder() {
        dictionary = new RegularTypeConverterDictionary();
    }
    
    public TypeConverterRegistry build() {
        return new CachedTypeConverterRegistry(new UncachedTypeConverterRegistry(dictionary));
    }
    
    public <S, T> TypeConverterRegistryBuilder register(Class<S> sourceClass, Class<T> targetClass, Function<S, T> converter) {
        dictionary.register(sourceClass, targetClass, converter);
        return this;
    }
    
}
