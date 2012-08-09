package com.youdevise.variance;

import com.google.common.base.Function;

public class TypeConverterRegistryBuilder {

    private final TypeConverterDictionary dictionary;
    
    public TypeConverterRegistryBuilder() {
        dictionary = new TypeConverterDictionary();
    }
    
    public TypeConverterRegistry build() {
        return new CachingTypeConverterRegistry(new UncachedTypeConverterRegistry(dictionary));
    }
    
    public <S, T> TypeConverterRegistryBuilder register(Class<S> sourceClass, Class<T> targetClass, Function<S, T> converter) {
        dictionary.register(sourceClass, targetClass, converter);
        return this;
    }
    
}
