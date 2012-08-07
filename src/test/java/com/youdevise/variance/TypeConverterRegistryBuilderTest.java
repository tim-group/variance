package com.youdevise.variance;

import org.junit.Test;

import com.google.common.base.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TypeConverterRegistryBuilderTest {

    @SuppressWarnings("rawtypes")
    @Test public void
    permits_chaining_of_register_calls() {
        TypeConverterRegistryBuilder builder = new TypeConverterRegistryBuilder();
        
        builder.register(Object.class, String.class, TypeConversions.toString)
               .register(Number.class, Integer.class, TypeConversions.toInt);
        
        TypeConverterRegistry registry = builder.build();
        
        assertThat(registry.getConverter(Integer.class, String.class), is((Function) TypeConversions.toString));
        assertThat(registry.getConverter(Double.class, Integer.class), is((Function) TypeConversions.toInt));
    }
    
}
