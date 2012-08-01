package com.youdevise.variance;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import com.youdevise.variance.ThreadLocalTypeConversionContext;
import com.youdevise.variance.TypeConversionContext;
import com.youdevise.variance.TypeConversionContexts;
import com.youdevise.variance.Variant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

public class VariantTest {

    private final Mockery context = new Mockery();
    
    @Test public void
    stores_a_value() {
        Variant variant = Variant.of("A string");
        
        assertThat(variant.as(String.class), is("A string"));
    }
    
    @SuppressWarnings("rawtypes")
    @Test public void
    exposes_the_type_of_its_value() {
        Variant variant = Variant.of(12L);
        
        assertThat(variant.valueClass(), equalTo((Class) Long.class));
    }
    
    @Test public void
    is_bound_to_a_thread_local_type_conversion_context() {
        Variant variant = Variant.of(null);
        
        TypeConversionContext ctx = ThreadLocalTypeConversionContext.getInstance();
        
        assertThat(variant.context(), sameInstance(ctx));
    }
    
    @Test public void
    bound_type_conversion_context_changes_when_thread_local_context_changes() {
        TypeConversionContext before = TypeConversionContexts.getDefault();
        ThreadLocalTypeConversionContext.setInstance(before);
        
        Variant variant = Variant.of(null);
        
        assertThat(variant.context(), sameInstance(before));
        
        TypeConversionContext after = TypeConversionContexts.getDefault();
        assertThat(before, not(sameInstance(after)));
        
        ThreadLocalTypeConversionContext.setInstance(after);
        assertThat(variant.context(), sameInstance(after));
    }
    
    @Test public void
    can_be_bound_to_custom_context() {
        TypeConversionContext newCtx = TypeConversionContexts.getDefault();
        
        Variant variant = Variant.of(null).in(newCtx);
        
        assertThat(variant.context(), sameInstance(newCtx));
    }
    
    @Test public void
    uses_context_to_convert_value_to_requested_type() {
        final TypeConversionContext mockCtx = context.mock(TypeConversionContext.class);
        
        context.checking(new Expectations() {{
            allowing(mockCtx).canConvert(12, String.class); will(returnValue(true));
            
            oneOf(mockCtx).convert(12, String.class); will(returnValue("The number 12"));
        }});
        
        Variant variant = Variant.of(12).in(mockCtx);
        
        assertThat(variant.as(String.class), is("The number 12"));
        context.assertIsSatisfied();
    }
    
    @Test(expected=IllegalArgumentException.class) public void
    throws_illegal_argument_exception_if_context_cannot_convert_value() {
        final TypeConversionContext mockCtx = context.mock(TypeConversionContext.class);
        
        context.checking(new Expectations() {{
            allowing(mockCtx).canConvert(12, String.class); will(returnValue(false));
        }});
        
        Variant.of(12).in(mockCtx).as(String.class);
    }
    
    
}
