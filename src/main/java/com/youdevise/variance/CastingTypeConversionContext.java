package com.youdevise.variance;

public class CastingTypeConversionContext implements TypeConversionContext {
    
    @Override public <C> boolean canConvert(Object o, Class<C> targetClass) {
        return targetClass.isAssignableFrom(o.getClass());
    }
    
    @SuppressWarnings("unchecked")
    @Override public <C> C convert(Object o, Class<C> targetClass) {
        return (C) o;
    }

    @Override
    public TypeConversionContext extendWith(TypeConversionContext ctx) {
        return new ChainedTypeConversionContext(ctx, this);
    }
}