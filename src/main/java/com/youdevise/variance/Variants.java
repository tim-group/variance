package com.youdevise.variance;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public final class Variants {
    
    public static final Function<Object, Variant> toVariant = new Function<Object, Variant>() {
        @Override public Variant apply(Object object) { return Variant.of(object); }
    };

    private Variants() { }

    public static final Function<Object, Variant> toVariant(TypeConversionContext context)  {
        return Functions.compose(Variants.inContext(context), toVariant);
    }

    public static <T> Function<Variant, T> variantTo(final Class<T> targetClass) {
        return new Function<Variant, T>() {
            @Override public T apply(Variant variant) { return variant.as(targetClass); }
        };
    }

    public static <T> Function<Variant, T> variantTo(Class<T> targetClass, TypeConversionContext context) {
        return Functions.compose(variantTo(targetClass), Variants.inContext(context));
    }

    public static Function<Variant, Variant> inContext(final TypeConversionContext context) {
        return new Function<Variant, Variant>() {
            @Override public Variant apply(Variant variant) {
                return variant.in(context);
            }
        };
    }
    
    
}