package com.youdevise.variance;

public final class ThreadLocalTypeConversionContext {
    
    private static final ThreadLocal<TypeConversionContext> threadLocal =
            new ThreadLocal<TypeConversionContext>() {
                @Override protected TypeConversionContext initialValue() {
                    return TypeConversionContexts.getDefault();
                }
    };
    
    public static TypeConversionContext getInstance() {
        return threadLocal.get();
    }
    
    public static void setInstance(TypeConversionContext newContext) {
        threadLocal.set(newContext);
    }

}
