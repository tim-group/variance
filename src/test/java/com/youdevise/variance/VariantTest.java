package com.youdevise.variance;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import com.google.common.collect.Iterables;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

public class VariantTest {

    private final Mockery context = new Mockery();
    
    private static final class TestTypeConversionContext implements TypeConversionContext {
        @Override
        public <C> boolean canConvert(Object o, Class<C> targetClass) {
            return TypeConversions.standardContext.canConvert(o, targetClass);
        }

        @Override
        public <C> C convert(Object o, Class<C> targetClass) {
            return TypeConversions.standardContext.convert(o, targetClass);
        }

        @Override
        public TypeConversionContext extendedWith(TypeConversionContext ctx) {
            return null;
        }
    }
    
    @Test public void
    stores_a_value() {
        Variant variant = Variant.of("A string");
        
        assertThat(variant.as(String.class), is("A string"));
    }
    
    @Test public void
    exposes_its_value_as_object_through_the_supplier_interface() {
        Variant variant = Variant.of("A string");
        
        assertThat(variant.get(), Matchers.<Object>equalTo("A string"));
    }
    
    @SuppressWarnings("rawtypes")
    @Test public void
    exposes_the_type_of_its_value() {
        Variant variant = Variant.of(12L);
        
        assertThat(variant.valueClass(), equalTo((Class) Long.class));
    }
    
    @Test public void
    knows_whether_it_is_convertible_to_a_given_type() {
        Variant variant = Variant.of(12L);
        
        assertThat(variant.isConvertibleTo(Thread.class), is(false));
        assertThat(variant.isConvertibleTo(Integer.class), is(true));
    }
    
    @Test public void
    is_bound_to_a_thread_local_type_conversion_context() {
        Variant variant = Variant.of(1);
        
        TypeConversionContext ctx = ImplicitTypeConversions.current();
        
        assertThat(variant.context(), sameInstance(ctx));
    }
    
    @Test public void
    bound_type_conversion_context_changes_when_thread_local_context_changes() {
        TypeConversionContext before = new TestTypeConversionContext();
        ImplicitTypeConversions.enterNew(before);
        
        Variant variant = Variant.of(1);
        
        assertThat(variant.context(), sameInstance(before));
        
        ImplicitTypeConversions.exit();
        
        TypeConversionContext after = new TestTypeConversionContext();
        ImplicitTypeConversions.enterNew(after);
        assertThat(variant.context(), sameInstance(after));
        ImplicitTypeConversions.exit();
    }
    
    @Test public void
    can_be_bound_to_custom_context() {
        TypeConversionContext newCtx = new TestTypeConversionContext();
        
        Variant variant = Variant.of(1).in(newCtx);
        
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
    
    @Test public void
    implements_number() {
        Variant variant = Variant.of(12);
        
        assertThat(variant.byteValue(), is((byte) 12));
        assertThat(variant.intValue(), is(12));
        assertThat(variant.floatValue(), is(12f));
        assertThat(variant.doubleValue(), is(12d));
        assertThat(variant.longValue(), is(12L));
        assertThat(variant.shortValue(), is((short) 12));
    }
    
    @Test public void
    can_return_an_array() {
        Variant variant = Variant.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        
        assertThat(variant.asArrayOf(Double.class), equalTo(new Double[] { 1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d ,9d }));
    }
    
    @Test public void
    can_return_an_iterable() {
        Variant variant = Variant.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        
        assertThat(variant.asIterableOf(Long.class), Matchers.<Long>hasItems(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L ,9L));
    }
    
    @Test public void
    returns_a_single_value_as_an_iterable() {
        assertThat(Variant.of(12).asIterableOf(Integer.class), Matchers.<Integer>hasItem(12));
    }
    
    @Test public void
    can_return_an_iterable_of_variants() {
        Variant variant = Variant.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        
        Iterable<Variant> variants = variant.asIterableOf(Variant.class);
        
        assertThat(variants.iterator().next().intValue(), is(1));
    }
    
    @Test public void
    can_be_initialised_with_a_primitive_array() {
        byte[] bytes = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[] ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        char[] chars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        short[] shorts = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        long[] longs = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        float[] floats = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        double[] doubles = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Double[] doubleObjects = { 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 };
        
        assertThat(Variant.of(bytes).asArrayOf(Double.class), equalTo(doubleObjects));
        assertThat(Variant.of(ints).asArrayOf(Double.class), equalTo(doubleObjects));
        assertThat(Variant.of(chars).asArrayOf(String.class), equalTo(new String[] { "a", "b", "c", "d", "e", "f", "g"}));
        assertThat(Variant.of(shorts).asArrayOf(Double.class), equalTo(doubleObjects));
        assertThat(Variant.of(longs).asArrayOf(Double.class), equalTo(doubleObjects));
        assertThat(Variant.of(floats).asArrayOf(Double.class), equalTo(doubleObjects));
        assertThat(Variant.of(doubles).asArrayOf(Double.class), equalTo(doubleObjects));
    }
    
    @Test public void
    iterable_members_are_bound_to_parent_context() {
        TypeConversionContext childContext = new TestTypeConversionContext();
        Variant child = Variant.of("1").in(childContext);
        Variant[] children = { child };

        Variant parent = Variant.of(children);
        assertThat(Iterables.getFirst(parent.asIterableOf(Variant.class), null).context(), Matchers.sameInstance(parent.context()));
    }
    
    @Test public void
    iterable_members_are_rebound_when_parent_is_transferred_to_new_context() {
        TypeConversionContext childContext = new TestTypeConversionContext();
        TypeConversionContext parentContext = new TestTypeConversionContext();
        final Variant child = Variant.of("1").in(childContext);
        Variant[] children = { child };
        
        Variant parent = Variant.of(children);
        Variant transferred = parent.in(parentContext);
        assertThat(Iterables.getFirst(transferred.asIterableOf(Variant.class), null).context(), Matchers.sameInstance(parentContext));
    }
}
