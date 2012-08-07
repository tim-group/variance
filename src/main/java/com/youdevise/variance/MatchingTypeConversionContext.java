package com.youdevise.variance;

import com.google.common.base.Function;

public class MatchingTypeConversionContext implements TypeConversionContext {

    public static final class Builder {
        private final TypeConverterRegistryBuilder registryBuilder = new TypeConverterRegistryBuilder();
        
        public <S, T> Builder register(Class<S> sourceClass, Class<T> targetClass, Function<S, T> converter) {
            registryBuilder.register(sourceClass, targetClass, converter);
            return this;
        }
        
        public TypeConversionContext build() {
            return new MatchingTypeConversionContext(registryBuilder.build());
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    private final TypeConverterRegistry registry;
    
    public MatchingTypeConversionContext(TypeConverterRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public <C> boolean canConvert(Object o, Class<C> targetClass) {
        return registry.hasConverter(o.getClass(), targetClass);
    }

    @Override
    public <C> C convert(Object o, Class<C> targetClass) {
        return convert(o.getClass(), targetClass, o);
    }
    
    @SuppressWarnings("unchecked")
    private <S, T> T convert(Class<S> sourceClass, Class<T> targetClass, Object o) {
        Function<? super S, ? extends T> converter = registry.getConverter(sourceClass, targetClass);
        return converter.apply((S) o);
    }

    @Override
    public TypeConversionContext extendWith(TypeConversionContext ctx) {
        return new ChainedTypeConversionContext(ctx, this);
    }

}
