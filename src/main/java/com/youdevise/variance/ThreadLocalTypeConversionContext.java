package com.youdevise.variance;

import java.util.Stack;

public final class ThreadLocalTypeConversionContext {
    
    private static final ThreadLocal<Stack<TypeConversionContext>> threadLocal =
            new ThreadLocal<Stack<TypeConversionContext>>() {
                @Override protected Stack<TypeConversionContext> initialValue() {
                    Stack<TypeConversionContext> contextStack = new Stack<TypeConversionContext>();
                    contextStack.push(new CastingTypeConversionContext());
                    return contextStack;
                }
    };
    
    public static TypeConversionContext current() {
        return threadLocal.get().peek();
    }

    public static void enter(TypeConversionContext context) {
        threadLocal.get().push(context);
    }
    
    public static void enterExtended(TypeConversionContext context) {
        TypeConversionContext old = current();
        enter(old.extendWith(context));
    }
    
    public static void exit() {
        Stack<TypeConversionContext> stack = threadLocal.get();
        if (stack.size()==1) {
            throw new IllegalStateException("Exit called without corresponding Enter");
        }
        stack.pop();
    }

}
