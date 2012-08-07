package com.youdevise.variance;

import java.util.concurrent.ExecutionException;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class CachedTypeConverterRegistry implements TypeConverterRegistry {

    private final TypeConverterRegistry innerRegistry;
    
    private final Cache<ConversionKey, Function<?, ?>> cache = CacheBuilder.newBuilder()
            .build(new CacheLoader<ConversionKey, Function<?, ?>>() {
                @Override public Function<?, ?> load(ConversionKey key) throws ConverterNotFoundException {
                    if (innerRegistry.hasConverter(key.sourceClass, key.targetClass)) {
                        return innerRegistry.getConverter(key.sourceClass, key.targetClass);
                    }
                    throw new ConverterNotFoundException();
                }
            });
    
    private static final class ConverterNotFoundException extends Exception {
        private static final long serialVersionUID = 560390370659936157L;
    }
    
    private static final class ConversionKey {
        private final Class<?> sourceClass;
        private final Class<?> targetClass;
        
        public ConversionKey(Class<?> sourceClass, Class<?> targetClass) {
            this.sourceClass = sourceClass;
            this.targetClass = targetClass;
        }
        
        @Override public boolean equals(Object other) {
            if (other instanceof ConversionKey) {
                return ((ConversionKey) other).sourceClass.equals(sourceClass)
                        && ((ConversionKey) other).targetClass.equals(targetClass);
            }
            return false;
        }
        
        @Override public int hashCode() {
            return Objects.hashCode(sourceClass, targetClass);
        }
    }
    
    public CachedTypeConverterRegistry(TypeConverterRegistry innerRegistry) {
        this.innerRegistry = innerRegistry;
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
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <S, T> Function<? super S, ? extends T> findConverter(Class<S> sourceClass, Class<T> targetClass) {
        ConversionKey key = new ConversionKey(sourceClass, targetClass);
        try {
            return (Function) cache.get(key);
        } catch (ExecutionException e) {
            return null;
        }
    }

}
