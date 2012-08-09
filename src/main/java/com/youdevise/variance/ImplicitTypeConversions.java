package com.youdevise.variance;

import java.util.Stack;

import com.google.common.base.Supplier;

public final class ImplicitTypeConversions {
    
    public static final TypeConversionContext implicitContext = new ProxyingTypeConversionContext(new Supplier<TypeConversionContext>() {
        @Override public TypeConversionContext get() { return current(); }
    });
    
    private static final ThreadLocal<Stack<TypeConversionContext>> threadLocal =
            new ThreadLocal<Stack<TypeConversionContext>>() {
                @Override protected Stack<TypeConversionContext> initialValue() {
                    Stack<TypeConversionContext> contextStack = new Stack<TypeConversionContext>();
                    return contextStack;
                }
    };
    
    public static TypeConversionContext current() {
        Stack<TypeConversionContext> stack = threadLocal.get();
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public static void enterNew(TypeConversionContext context) {
        threadLocal.get().push(context);
    }
    
    public static void enterExtended(TypeConversionContext context) {
        TypeConversionContext old = current();
        if (old == null) {
            enterNew(context);
        } else {
            enterNew(old.extendedWith(context));
        }
    }
    
    public static void exit() {
        Stack<TypeConversionContext> stack = threadLocal.get();
        if (stack.size()==0) {
            throw new IllegalStateException("Exit called without corresponding Enter");
        }
        stack.pop();
    }

}
