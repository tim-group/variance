package com.youdevise.variance;

public interface TypeConversionContext {
    <C> boolean canConvert(Object o, Class<C> targetClass);
    <C> C convert(Object o, Class<C> targetClass);
}
