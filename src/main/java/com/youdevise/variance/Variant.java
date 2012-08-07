package com.youdevise.variance;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class Variant extends Number {

    private static final long serialVersionUID = 6200248721405100437L;

    public static Variant of(Object value) {
        return new Variant(value);
    }
    
    private final Object value;
    private final Supplier<TypeConversionContext> typeConversionContextSupplier;
    
    private Variant(Object value) {
        this.value = value;
        this.typeConversionContextSupplier = new Supplier<TypeConversionContext>() {
            @Override public TypeConversionContext get() {
                return ThreadLocalTypeConversionContext.current();
            }
        };
    }
    
    private Variant(Object value, Supplier<TypeConversionContext> typeConversionContextSupplier) {
        this.value = value;
        this.typeConversionContextSupplier = typeConversionContextSupplier;
    }
    
    public <C> C as(Class<C> targetClass) {
        Preconditions.checkArgument(context().canConvert(value, targetClass),
                                    "Unable to convert a value of type [%s] to [%s] in the current context",
                                    value.getClass(),
                                    targetClass);
        return context().convert(value, targetClass);
    }
    
    public Variant in(TypeConversionContext ctx) {
        return new Variant(value, Suppliers.ofInstance(ctx));
    }

    public Class<?> valueClass() {
        return value.getClass();
    }

    public TypeConversionContext context() {
        return typeConversionContextSupplier.get();
    }

    @Override
    public int intValue() {
        return as(Integer.class);
    }

    @Override
    public long longValue() {
        return as(Long.class);
    }

    @Override
    public float floatValue() {
        return as(Float.class);
    }

    @Override
    public double doubleValue() {
        return as(Double.class);
    }
    
    @Override
    public String toString() {
        return as(String.class);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Variant) {
            return Objects.equal(((Variant) o).value, value);
        }
        return false;
    }
}
