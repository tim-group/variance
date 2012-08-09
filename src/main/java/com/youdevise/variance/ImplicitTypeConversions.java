package com.youdevise.variance;

import java.util.Stack;

import com.google.common.base.Supplier;

public final class ImplicitTypeConversions {
    
    public static final Supplier<TypeConversionContext> supplier = new Supplier<TypeConversionContext>() {
        @Override public TypeConversionContext get() {
            return current();
        }
    };
    
    private static final ThreadLocal<Stack<TypeConversionContext>> threadLocal =
            new ThreadLocal<Stack<TypeConversionContext>>() {
                @Override protected Stack<TypeConversionContext> initialValue() {
                    Stack<TypeConversionContext> contextStack = new Stack<TypeConversionContext>();
                    contextStack.push(TypeConversions.standardContext);
                    return contextStack;
                }
    };
    
    public static TypeConversionContext current() {
        return threadLocal.get().peek();
    }

    public static void enterNew(TypeConversionContext context) {
        threadLocal.get().push(context);
    }
    
    public static void enterExtended(TypeConversionContext context) {
        TypeConversionContext old = current();
        enterNew(old.extendedWith(context));
    }
    
    public static void exit() {
        Stack<TypeConversionContext> stack = threadLocal.get();
        if (stack.size()==1) {
            throw new IllegalStateException("Exit called without corresponding Enter");
        }
        stack.pop();
    }

}
