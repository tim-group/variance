package com.youdevise.variance;

import java.util.Map;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.youdevise.variance.UncachedTypeConverterRegistry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UncachedTypeConverterRegistryTest {
    
    private static interface Top { }
    private static interface Upper extends Top { }
    private static interface Left extends Upper { }
    private static interface Right extends Upper { }
    private static interface Lower extends Left, Right { }
    private static interface Bottom extends Lower { }
    
    private static final Function<Left, Right> leftToRight = new Function<Left, Right>() {
        @Override public Right apply(Left arg0) { return null; }
    };
    
    private static final Function<Upper, Right> upperToRight = new Function<Upper, Right>() {
        @Override public Right apply(Upper arg0) { return null; }
    };
    
    private static final Function<Top, Left> topToLeft = new Function<Top, Left>() {
        @Override public Left apply(Top arg0) { return null; }
    };
    
    private static final Function<Top, Upper> topToUpper = new Function<Top, Upper>() {
        @Override public Upper apply(Top arg0) { return null; }
    };
    
    private static final Function<Top, Right> topToRight = new Function<Top, Right>() {
        @Override public Right apply(Top arg0) { return null; }
    };
    
    private static final Function<Upper, Lower> upperToLower = new Function<Upper, Lower>() {
        @Override public Lower apply(Upper arg0) { return null; }
    };
    
    private static final Function<Upper, Bottom> upperToBottom = new Function<Upper, Bottom>() {
        @Override public Bottom apply(Upper arg0) { return null; }
    };
    
    private static final Function<Left, Bottom> leftToBottom = new Function<Left, Bottom>() {
        @Override public Bottom apply(Left arg0) { return null; }
    };
    
    private static final Function<Right, Bottom> rightToBottom = new Function<Right, Bottom>() {
        @Override public Bottom apply(Right arg0) { return null; }
    };
    
    private static final Function<Lower, Bottom> lowerToBottom = new Function<Lower, Bottom>() {
        @Override public Bottom apply(Lower arg0) { return null; }
    };
    
    @SuppressWarnings("rawtypes")
    @Test public void
    finds_converter_matching_types_exactly() {
        UncachedTypeConverterRegistry registry = new UncachedTypeConverterRegistry();

        registry.register(Left.class, Right.class, leftToRight);
        
        assertThat(registry.getConverter(Left.class, Right.class), is((Function) leftToRight));
    }
    
    @SuppressWarnings("rawtypes")
    @Test public void
    finds_converter_matching_supertype_of_source_type() {
        UncachedTypeConverterRegistry registry = new UncachedTypeConverterRegistry();

        registry.register(Upper.class, Right.class, upperToRight);
        
        assertThat(registry.getConverter(Left.class, Right.class), is((Function) upperToRight));
    }
    
    @SuppressWarnings("rawtypes")
    @Test public void
    prioritises_closest_supertype_of_source_type() {
        Map<Class<?>, Map<Class<?>, Function<?, ?>>> wrappedMap = Maps.newLinkedHashMap();
        UncachedTypeConverterRegistry registry = new UncachedTypeConverterRegistry(wrappedMap);

        registry.register(Top.class, Right.class, topToRight);
        registry.register(Upper.class, Right.class, upperToRight);
        
        assertThat(registry.getConverter(Left.class, Right.class), is((Function) upperToRight));
        assertThat(registry.getConverter(Upper.class, Right.class), is((Function) upperToRight));
        assertThat(registry.getConverter(Top.class, Right.class), is((Function) topToRight));
    }
    
    @Test(expected=IllegalArgumentException.class) public void
    throws_exception_if_two_incomparable_classes_are_both_nearest_supertype_of_source_type() {
        UncachedTypeConverterRegistry registry = new UncachedTypeConverterRegistry();

        registry.register(Left.class, Bottom.class, leftToBottom);
        registry.register(Right.class, Bottom.class, rightToBottom);
        
        registry.getConverter(Lower.class, Bottom.class);
    }
    
    @SuppressWarnings("rawtypes")
    @Test public void
    does_not_throw_exception_if_two_incomparable_classes_are_supertypes_of_source_type_but_not_nearest() {
        UncachedTypeConverterRegistry registry = new UncachedTypeConverterRegistry();

        registry.register(Left.class, Bottom.class, leftToBottom);
        registry.register(Right.class, Bottom.class, rightToBottom);
        registry.register(Lower.class, Bottom.class, lowerToBottom);
        
        assertThat(registry.getConverter(Lower.class, Bottom.class), is((Function) lowerToBottom));
    }
    
    @SuppressWarnings("rawtypes")
    @Test public void
    finds_converter_matching_subtype_of_target_type() {
        UncachedTypeConverterRegistry registry = new UncachedTypeConverterRegistry();

        registry.register(Upper.class, Bottom.class, upperToBottom);
        
        assertThat(registry.getConverter(Upper.class, Left.class), is((Function) upperToBottom));
    }
    
    @SuppressWarnings("rawtypes")
    @Test public void
    prioritises_nearest_subtype_of_target_type() {
        UncachedTypeConverterRegistry registry = new UncachedTypeConverterRegistry();

        registry.register(Upper.class, Lower.class, upperToLower);
        registry.register(Upper.class, Bottom.class, upperToBottom);
        
        assertThat(registry.getConverter(Upper.class, Left.class), is((Function) upperToLower));
        assertThat(registry.getConverter(Upper.class, Lower.class), is((Function) upperToLower));
        assertThat(registry.getConverter(Upper.class, Bottom.class), is((Function) upperToBottom));
    }
    
    @Test(expected=IllegalArgumentException.class) public void
    throws_exception_if_two_incomparable_classes_are_both_nearest_subtype_of_target_type() {
        UncachedTypeConverterRegistry registry = new UncachedTypeConverterRegistry();

        registry.register(Top.class, Left.class, topToLeft);
        registry.register(Top.class, Right.class, topToRight);
        
        registry.getConverter(Top.class, Upper.class);
    }
    
    @SuppressWarnings("rawtypes")
    @Test public void
    does_not_throw_exception_if_two_incomparable_classes_are_descendents_of_target_type_but_not_nearest_subtypes() {
        UncachedTypeConverterRegistry registry = new UncachedTypeConverterRegistry();

        registry.register(Top.class, Left.class, topToLeft);
        registry.register(Top.class, Right.class, topToRight);
        registry.register(Top.class, Upper.class, topToUpper);
        
        assertThat(registry.getConverter(Top.class, Upper.class), is((Function) topToUpper));
    }
}
