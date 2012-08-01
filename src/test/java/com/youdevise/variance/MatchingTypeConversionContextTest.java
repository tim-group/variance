package com.youdevise.variance;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import com.google.common.base.Function;
import com.youdevise.variance.MatchingTypeConversionContext;
import com.youdevise.variance.TypeConverterRegistry;

import static org.hamcrest.MatcherAssert.assertThat;

public class MatchingTypeConversionContextTest {
    
    private final Mockery context = new Mockery();
    
    @Test public void
    can_convert_if_registry_contains_suitable_converter() {
        final TypeConverterRegistry mockRegistry = context.mock(TypeConverterRegistry.class);
        
        context.checking(new Expectations() {{
            oneOf(mockRegistry).hasConverter(Integer.class, String.class); will(returnValue(true));
        }});
        MatchingTypeConversionContext ctx = new MatchingTypeConversionContext(mockRegistry);
                
        Object o = 12;
        assertThat(ctx.canConvert(o, String.class), Matchers.is(true));
    }
    
    @Test public void
    cannot_convert_if_registry_does_not_contain_a_suitable_converter() {
        final TypeConverterRegistry mockRegistry = context.mock(TypeConverterRegistry.class);
        
        context.checking(new Expectations() {{
            oneOf(mockRegistry).hasConverter(Integer.class, String.class); will(returnValue(false));
        }});
        MatchingTypeConversionContext ctx = new MatchingTypeConversionContext(mockRegistry);
                
        Object o = 12;
        assertThat(ctx.canConvert(o, String.class), Matchers.is(false));
    }
    
    @SuppressWarnings("unchecked")
    @Test public void
    uses_converter_supplied_by_registry_to_convert_value() {
        final TypeConverterRegistry mockRegistry = context.mock(TypeConverterRegistry.class);
        final Function<Integer, String> converter = context.mock(Function.class);
        
        context.checking(new Expectations() {{
            allowing(mockRegistry).getConverter(Integer.class, String.class); will(returnValue(converter));
            
            oneOf(converter).apply(12); will(returnValue("The number 12"));
        }});
        MatchingTypeConversionContext ctx = new MatchingTypeConversionContext(mockRegistry);
                
        Object o = 12;
        assertThat(ctx.convert(o, String.class), Matchers.is("The number 12"));
    }
}
