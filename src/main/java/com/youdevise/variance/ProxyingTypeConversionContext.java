package com.youdevise.variance;

import com.google.common.base.Supplier;

public final class ProxyingTypeConversionContext implements TypeConversionContext {
    
    private final Supplier<TypeConversionContext> supplier;
    
    public ProxyingTypeConversionContext(Supplier<TypeConversionContext> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public TypeConversionContext extendedWith(TypeConversionContext ctx) {
        return supplier.get().extendedWith(ctx);
    }
    
    @Override
    public <C> C convert(Object o, Class<C> targetClass) {
        return supplier.get().convert(o, targetClass);
    }
    
    @Override
    public <C> boolean canConvert(Object o, Class<C> targetClass) {
        if (supplier.get() == null) { return false; }
        return supplier.get().canConvert(o, targetClass);
    }
}