package com.youdevise.variance;

public final class TypeConversionContexts {

    private TypeConversionContexts() { }
    
    public static TypeConversionContext getDefault() {
        return new StringifyingTypeConversionContext();
    }
    
    private static class StringifyingTypeConversionContext implements TypeConversionContext {
        
        @Override public <C> boolean canConvert(Object o, Class<C> targetClass) {
            return targetClass.isAssignableFrom(o.getClass());
        }
        
        @SuppressWarnings("unchecked")
        @Override public <C> C convert(Object o, Class<C> targetClass) {
            if (targetClass.equals(String.class)) {
                return (C) o.toString();
            }
            return (C) o;
        }
    }
    
}
