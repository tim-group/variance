package com.youdevise.variance;

import com.google.common.base.Function;

public interface TypeConverterRegistry {
    boolean hasConverter(Class<?> sourceType, Class<?> targetType);
    <S, T> Function<? super S, ? extends T> getConverter(Class<S> sourceType, Class<T> targetType);
}
