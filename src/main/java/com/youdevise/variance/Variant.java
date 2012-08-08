package com.youdevise.variance;

import java.lang.reflect.Array;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Variant extends Number implements Supplier<Object> {

    private static final long serialVersionUID = 6200248721405100437L;

    public static Variant of(Object value) {
        return new Variant(value);
    }
    
    public static Variant of(Object[] values) {
        return new Variant(Lists.newArrayList(values));
    }
    
    public static Variant of(int[] values) {
        return of(toBoxedArray(Integer.class, values));
    }
    
    public static Variant of(byte[] values) {
        return of(toBoxedArray(Byte.class, values));
    }
    
    public static Variant of(short[] values) {
        return of(toBoxedArray(Short.class, values));
    }
    
    public static Variant of(long[] values) {
        return of(toBoxedArray(Long.class, values));
    }
    
    public static Variant of(float[] values) {
        return of(toBoxedArray(Float.class, values));
    }
    
    public static Variant of(double[] values) {
        return of(toBoxedArray(Double.class, values));
    }
    
    public static Variant of(char[] values) {
        return of(toBoxedArray(Character.class, values));
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T[] toBoxedArray(Class<T> boxClass, Object components) {
        final int length = Array.getLength(components);
        Object res = Array.newInstance(boxClass, length);

        for (int i = 0; i < length; i++) {
            Array.set(res, i, Array.get(components, i));
        }

        return (T[]) res;
    }

    public static Variant of(Object firstValue, Object...moreValues) {
        return new Variant(Lists.asList(firstValue, moreValues));
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
        return new Variant(value, Suppliers.ofInstance(ctx));
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
