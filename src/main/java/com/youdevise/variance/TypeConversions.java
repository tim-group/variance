package com.youdevise.variance;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public final class TypeConversions {
    private TypeConversions() { }
    
    public static final Function<Object, String> toString = Functions.toStringFunction();
    
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
    
}
