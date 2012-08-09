package com.youdevise.variance;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public final class Variants {
    
    public static final Function<Object, Variant> toVariant = new Function<Object, Variant>() {
        @Override public Variant apply(Object object) { return Variant.of(object); }
    };

    private Variants() { }

    public static final Function<Object, Variant> toVariant(TypeConversionContext context)  {
        return Variants.toVariant(Suppliers.ofInstance(context));
    }

    public static final Function<Object, Variant> toVariant(Supplier<TypeConversionContext> context)  {
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

    public static <T> Function<Variant, T> variantTo(Class<T> targetClass, Supplier<TypeConversionContext> context) {
        return Functions.compose(variantTo(targetClass), Variants.inContext(context));
    }

    public static Function<Variant, Variant> inContext(TypeConversionContext context) {
        return Variants.inContext(Suppliers.ofInstance(context));
    }

    public static Function<Variant, Variant> inContext(final Supplier<TypeConversionContext> context) {
        return new Function<Variant, Variant>() {
            @Override public Variant apply(Variant variant) {
                return variant.in(context);
            }
        };
    }
    
    
}