package com.youdevise.variance;

public class ChainedTypeConversionContext implements TypeConversionContext {

    private final TypeConversionContext primary;
    private final TypeConversionContext secondary;
    
    public ChainedTypeConversionContext(TypeConversionContext primary, TypeConversionContext secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public <C> boolean canConvert(Object o, Class<C> targetClass) {
        return primary.canConvert(o, targetClass) || secondary.canConvert(o, targetClass);
    }

    @Override
    public <C> C convert(Object o, Class<C> targetClass) {
        if (primary.canConvert(o, targetClass)) {
            return primary.convert(o, targetClass);
        }
        return secondary.convert(o, targetClass);
    }

    @Override
    public TypeConversionContext extendedWith(TypeConversionContext ctx) {
        return new ChainedTypeConversionContext(ctx, this);
    }
    
}
