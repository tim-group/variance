package com.youdevise.variance;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public final class TypeConversions {
    private TypeConversions() { }
    
    public static final Function<Number, Byte> toByte = new Function<Number, Byte>() {
        @Override public Byte apply(Number number) { return number.byteValue(); }
    };
    
    public static final Function<Number, Integer> toInt = new Function<Number, Integer>() {
        @Override public Integer apply(Number number) { return number.intValue(); }
    };
    
    public static final Function<Number, Short> toShort = new Function<Number, Short>() {
        @Override public Short apply(Number number) { return number.shortValue(); }
    };
    
    public static final Function<Number, Long> toLong = new Function<Number, Long>() {
        @Override public Long apply(Number number) { return number.longValue(); }
    };
    
    public static final Function<Number, Double> toDouble = new Function<Number, Double>() {
        @Override public Double apply(Number number) { return number.doubleValue(); }
    };
    
    public static final Function<Number, Float> toFloat = new Function<Number, Float>() {
        @Override public Float apply(Number number) { return number.floatValue(); }
    };
    
    public static final Function<Object, String> toString = Functions.toStringFunction();
    
    @SuppressWarnings("rawtypes")
    public static final Function<String, Iterable> stringToIterable = new Function<String, Iterable>() {
        @Override public Iterable apply(String string) {
            return Splitter.on(",").split(string);
        }
    };
    
    @SuppressWarnings("rawtypes")
    public static final Function<Object, Iterable> objectToIterable = new Function<Object, Iterable>() {
        @Override public Iterable apply(Object object) {
            return Lists.newArrayList(object);
        }
    };
    
    @SuppressWarnings("rawtypes")
    public static final Function<Iterable, String> iterableToString = new Function<Iterable, String>() {
        @Override public String apply(Iterable parts) {
            return Joiner.on(",").join(parts);
        }
    };
    
    public static final Function<String, Byte> stringToByte = new Function<String, Byte>() {
        @Override public Byte apply(String string) { return Byte.valueOf(string); }
    };
    
    public static final Function<String, Integer> stringToInt = new Function<String, Integer>() {
        @Override public Integer apply(String string) { return Integer.valueOf(string); }
    };
    
    public static final Function<String, Short> stringToShort = new Function<String, Short>() {
        @Override public Short apply(String string) { return Short.valueOf(string); }
    };

    public static final Function<String, Long> stringToLong = new Function<String, Long>() {
        @Override public Long apply(String string) { return Long.valueOf(string); }
    };
    
    public static final Function<String, Float> stringToFloat = new Function<String, Float>() {
        @Override public Float apply(String string) { return Float.valueOf(string); }
    };
    
    public static final Function<String, Double> stringToDouble = new Function<String, Double>() {
        @Override public Double apply(String string) { return Double.valueOf(string); }
    };
    
    public static final TypeConversionContext standardContext = MatchingTypeConversionContext.builder()
        .register(Number.class, Byte.class, toByte)
        .register(Number.class, Integer.class, toInt)
        .register(Number.class, Short.class, toShort)
        .register(Number.class, Long.class, toLong)
        .register(Number.class, Float.class, toFloat)
        .register(Number.class, Double.class, toDouble)
        .register(Object.class, String.class, toString)
        .register(String.class, Byte.class, stringToByte)
        .register(String.class, Integer.class, stringToInt)
        .register(String.class, Short.class, stringToShort)
        .register(String.class, Long.class, stringToLong)
        .register(String.class, Float.class, stringToFloat)
        .register(String.class, Double.class, stringToDouble)
        .register(String.class, Iterable.class, stringToIterable)
        .register(Iterable.class, String.class, iterableToString)
        .register(Object.class, Iterable.class, objectToIterable)
        .build();

}
