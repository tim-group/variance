package com.youdevise.variance;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import com.google.common.base.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CachedTypeConverterRegistryTest {

    private final Mockery context = new Mockery();
    
    private final Function<Integer, String> intToString = new Function<Integer, String>() {
        @Override public String apply(Integer arg0) { return arg0.toString(); }
    };
    
    @SuppressWarnings("rawtypes")
    @Test public void
    cache_misses_are_handled_by_the_wrapped_type_converter_registry() {
        final TypeConverterRegistry mockRegistry = context.mock(TypeConverterRegistry.class);
        
        context.checking(new Expectations() {{
            oneOf(mockRegistry).hasConverter(Integer.class, String.class); will(returnValue(true));
            oneOf(mockRegistry).getConverter(Integer.class, String.class); will(returnValue(intToString));
        }});
        
        CachedTypeConverterRegistry cachedRegistry = new CachedTypeConverterRegistry(mockRegistry);
        
        assertThat(cachedRegistry.getConverter(Integer.class, String.class), is((Function) intToString));
        context.assertIsSatisfied();
    }
    
    @SuppressWarnings("rawtypes")
    @Test public void
    cache_hits_are_returned_directly_from_the_cache() {
        final TypeConverterRegistry mockRegistry = context.mock(TypeConverterRegistry.class);
        
        context.checking(new Expectations() {{
            oneOf(mockRegistry).hasConverter(Integer.class, String.class); will(returnValue(true));
            oneOf(mockRegistry).getConverter(Integer.class, String.class); will(returnValue(intToString));
        }});
        
        CachedTypeConverterRegistry cachedRegistry = new CachedTypeConverterRegistry(mockRegistry);
        
        assertThat(cachedRegistry.getConverter(Integer.class, String.class), is((Function) intToString));
        assertThat(cachedRegistry.getConverter(Integer.class, String.class), is((Function) intToString));
        context.assertIsSatisfied();
    }
    
    @Test(expected=NullPointerException.class) public void
    converters_not_in_the_inner_registry_are_not_returned() {
        final TypeConverterRegistry mockRegistry = context.mock(TypeConverterRegistry.class);
        
        context.checking(new Expectations() {{
            allowing(mockRegistry).hasConverter(Integer.class, String.class); will(returnValue(false));
        }});
        
        CachedTypeConverterRegistry cachedRegistry = new CachedTypeConverterRegistry(mockRegistry);
        
        assertThat(cachedRegistry.hasConverter(Integer.class, String.class), is(false));
        cachedRegistry.getConverter(Integer.class, String.class);
    }
    
}
