package com.youdevise.variance;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Variant extends Number implements Supplier<Object> {

    private static final long serialVersionUID = 6200248721405100437L;
    
    @SuppressWarnings({ "rawtypes" })
    public static Variant of(Object value) {
        Preconditions.checkNotNull(value, "A variant cannot have a null value.");
        
        if (value instanceof Variant) {
            return (Variant) value;
        }
        
        if (value.getClass().isArray()) {
            return ofVariants(arrayToVariants(value));
        }
        
        if (value instanceof Iterable) {
            return of((Iterable) value);
        }
        
        return new Variant(value);
    }
    
    public static Variant of(Object firstValue, Object...moreValues) {
        return of(Lists.asList(firstValue, moreValues));
    }
    
    public static Variant of(Iterable<?> values) {
        return ofVariants(Iterables.transform(values, Variants.toVariant));
    }

    public static Variant ofVariants(Variant...variants) {
        return ofVariants(Lists.newArrayList(variants));
    }
    
    public static Variant ofVariants(Iterable<Variant> variants) {
        return new Variant(variants);
    }
    
    private static Iterable<Variant> arrayToVariants(Object value) {
        Collection<Variant> results = new ArrayList<Variant>(Array.getLength(value));
        for (int i=0; i<Array.getLength(value); i++) {
            results.add(Variant.of(Array.get(value, i)));
        }
        return results;
    }
    
    private final Object value;
    private final Supplier<TypeConversionContext> typeConversionContextSupplier;
    
    private Variant(Object value) {
        this(value, ImplicitTypeConversions.supplier);
    }
    
    
    @SuppressWarnings("unchecked")
    private Variant(Object value, Supplier<TypeConversionContext> typeConversionContextSupplier) {
        if (value instanceof Iterable) {
            this.value = bound((Iterable<Variant>) value, this);
        } else {
            this.value = value;
        }
        this.typeConversionContextSupplier = typeConversionContextSupplier;
    }

    private Iterable<Variant> bound(Iterable<Variant> values, final Variant parent) {
        Supplier<TypeConversionContext> supplier = new Supplier<TypeConversionContext>() {
            @Override public TypeConversionContext get() {
                return parent.context();
            }
        };
        return Iterables.transform(values, Variants.inContext(supplier));
    }
    
    public <C> C as(Class<C> targetClass) {
        Preconditions.checkArgument(context().canConvert(value, targetClass),
                                    "Unable to convert a value of type [%s] to [%s] in the current context",
                                    value.getClass(),
                                    targetClass);
        return context().convert(value, targetClass);
    }
    
    @SuppressWarnings({ "unchecked" })
    public <C> Iterable<C> asIterableOf(final Class<C> targetClass) {
        return Iterables.transform(as(Iterable.class), new Function<Object, C>() {
            @Override public C apply(Object o) {
                return context().convert(o, targetClass);
            }
        });
    }
    
    public <C> C[] asArrayOf(final Class<C> targetClass) {
        Iterable<C> iterable = asIterableOf(targetClass);
        return Iterables.toArray(iterable, targetClass);
    }
    
    public Variant in(TypeConversionContext ctx) {
        return in(Suppliers.ofInstance(ctx));
    }
    
    public Variant in(Supplier<TypeConversionContext> ctx) {
        return new Variant(value, ctx);
    }

    public Class<?> valueClass() {
        return value.getClass();
    }

    public TypeConversionContext context() {
        return typeConversionContextSupplier.get();
    }
    
    public boolean isConvertibleTo(Class<?> targetClass) {
        return context().canConvert(value, targetClass);
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

    @Override
    public Object get() {
        return value;
    }
}
