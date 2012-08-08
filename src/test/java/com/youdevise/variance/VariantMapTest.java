package com.youdevise.variance;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class VariantMapTest {

    @Test public void
    converts_keys_and_values_to_variants_on_put() {
        VariantMap map = new VariantMap();
        
        map.put("The meaning of life", "42");
        assertThat(map.get("The meaning of life").intValue(), is(42));
        
        assertThat(map.containsKey("The meaning of life"), is(true));
        assertThat(map.containsKey(Variant.of("The meaning of life")), is(true));
    }
    
    @Test public void
    converts_key_to_variants_on_get() {
        VariantMap map = new VariantMap();
        
        map.put(Variant.of("The meaning of life"), "42");
        assertThat(map.get("The meaning of life").intValue(), is(42));
    }
    
}
