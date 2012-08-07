package com.youdevise.variance;

import java.util.Stack;

public final class ThreadLocalTypeConversionContext {
    
    private static final ThreadLocal<Stack<TypeConversionContext>> threadLocal =
            new ThreadLocal<Stack<TypeConversionContext>>() {
                @Override protected Stack<TypeConversionContext> initialValue() {
                    Stack<TypeConversionContext> contextStack = new Stack<TypeConversionContext>();
                    contextStack.push(standardTypeConversionContext());
                    return contextStack;
                }
    };
    
    private static final TypeConversionContext standardTypeConversionContext() {
        TypeConversionContext castingContext = new CastingTypeConversionContext();
        
        TypeConversionContext numericContext = MatchingTypeConversionContext.builder()
            .register(Number.class, Byte.class, TypeConversions.toByte)
            .register(Number.class, Integer.class, TypeConversions.toInt)
            .register(Number.class, Short.class, TypeConversions.toShort)
            .register(Number.class, Long.class, TypeConversions.toLong)
            .register(Number.class, Float.class, TypeConversions.toFloat)
            .register(Number.class, Double.class, TypeConversions.toDouble)
            .register(Object.class, String.class, TypeConversions.toString)
            .build();
        
        return castingContext.extendWith(numericContext);
    }
    
    public static TypeConversionContext current() {
        return threadLocal.get().peek();
    }

    public static void enterNew(TypeConversionContext context) {
        threadLocal.get().push(context);
    }
    
    public static void enterExtended(TypeConversionContext context) {
        TypeConversionContext old = current();
        enterNew(old.extendWith(context));
    }
    
    public static void exit() {
        Stack<TypeConversionContext> stack = threadLocal.get();
        if (stack.size()==1) {
            throw new IllegalStateException("Exit called without corresponding Enter");
        }
        stack.pop();
    }

}
