package com.youdevise.variance;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TypeConversionExamples {

    @Before public void
    setup_local_type_conversion_context() {
        MatchingTypeConversionContext.Builder builder = MatchingTypeConversionContext.builder();
        
        builder.register(Number.class, Integer.class, new Function<Number, Integer>() {
            @Override public Integer apply(Number arg0) { return arg0.intValue(); }
        });
        
        builder.register(Number.class, String.class, new Function<Number, String>() {
            @Override public String apply(Number arg0) {
                DecimalFormat format = new DecimalFormat("#,###,###,##0.00");
                return format.format(arg0.doubleValue());
            }
        });
        
        builder.register(String.class, Date.class, new Function<String, Date>() {
            @Override
            public Date apply(String arg0) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    return format.parse(arg0);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        
        builder.register(Date.class, String.class, new Function<Date, String>() {
            @Override
            public String apply(Date arg0) {
                DateFormat format = new SimpleDateFormat("dd MMMM yyyy");
                return format.format(arg0);
            }
        });
                
        ThreadLocalTypeConversionContext.enterExtended(builder.build());
    }
    
    @After public void
    exit_type_conversion_context() {
        ThreadLocalTypeConversionContext.exit();
    }
    
    @Test public void
    every_kind_of_number_to_int() {
        assertThat(Variant.of(12).as(Integer.class), is(12));
        assertThat(Variant.of(12L).as(Integer.class), is(12));
        assertThat(Variant.of(12.1f).as(Integer.class), is(12));
        assertThat(Variant.of(12.1d).as(Integer.class), is(12));
        assertThat(Variant.of(new BigDecimal(12)).as(Integer.class), is(12));
    }
    
    @Test public void
    every_kind_of_number_to_2dp_string() {
        assertThat(Variant.of(12).as(String.class), is("12.00"));
        assertThat(Variant.of(12L).as(String.class), is("12.00"));
        assertThat(Variant.of(12.34f).as(String.class), is("12.34"));
        assertThat(Variant.of(12.34d).as(String.class), is("12.34"));
        assertThat(Variant.of(new BigDecimal(12)).as(String.class), is("12.00"));
    }
    
    @Test public void
    string_to_date_and_date_to_string() {
        Date today = Variant.of("2012-07-31").as(Date.class);
        assertThat(Variant.of(today).as(String.class), is("31 July 2012"));
    }
    
    @Test public void
    locally_modifying_context() {
        MatchingTypeConversionContext.Builder builder = MatchingTypeConversionContext.builder();
        
        builder.register(Number.class, String.class, new Function<Number, String>() {
            @Override public String apply(Number arg0) {
                DecimalFormat format = new DecimalFormat("#,###,###,##0.0000");
                return format.format(arg0.doubleValue());
            }
        });
        
        TypeConversionContext fourDigitContext = builder.build();
        
        assertThat(Variant.of(12.3456f).as(String.class), is("12.35"));
        assertThat(Variant.of(12.3456f).in(fourDigitContext).as(String.class), is("12.3456"));
    }
    
    @Test public void
    comma_separated_string_to_array() {
        assertThat(Variant.of("1,2,3").asArrayOf(Double.class), equalTo(new Double[] { 1d, 2d, 3d }));
        assertThat(Variant.of("1,2,3").asArrayOf(String.class), equalTo(new String[] { "1", "2", "3" }));
    }
    
    @Test public void
    array_to_a_comma_separated_string() {
        assertThat(Variant.of(1, 2, 3).toString(), equalTo("1,2,3"));
    }
}
