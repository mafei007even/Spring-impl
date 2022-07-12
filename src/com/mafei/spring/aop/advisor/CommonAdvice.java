package com.mafei.spring.aop.advisor;

import com.mafei.spring.aop.AspectInstanceFactory;
import com.mafei.spring.aop.advisor.joinpoint.ProceedingJoinPoint;
import com.mafei.spring.core.Ordered;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author mafei007
 * @date 2022/7/7 23:04
 */
public abstract class CommonAdvice implements Advice, Ordered {

    private Method aspectJAdviceMethod;
    private AspectInstanceFactory aspectInstanceFactory;

    /**
     * 默认拦截所有的异常类型
     */
    private Class<?> discoveredThrowingType = Object.class;

    /**
     * @AfterThrowing( value = "execution(* foo())", throwing = "java.lang.ClassNotFoundException")
     * throwing 的值
     */
    private String throwingName;

    public CommonAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        aspectJAdviceMethod.setAccessible(true);
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.aspectInstanceFactory = aspectInstanceFactory;
    }


    /**
     * 调用通知方法，完成简单的参数回显解析
     *
     * @return
     */
    protected Object invokeAdviceMethod(ProceedingJoinPoint pjp, Throwable ex) throws Throwable {
        // 准备方法参数
        int parameterCount = this.aspectJAdviceMethod.getParameterCount();
        Object[] args = new Object[parameterCount];

        // 存在异常，@AfterThrowing 通知的调用
        if (ex != null) {
            // 设置了 throwingName，但没有通知方法中没有参数，报错
            // 例：对应默认的 @AfterThrowing ，注解中不设置 throwing
            if (parameterCount == 0) {
                if (this.throwingName != null) {
                    throw new IllegalStateException("Throwing argument name '" + this.throwingName +
                            "' was not bound in advice arguments");
                }
            } else {
                // 通知方法如果有参数的话，第一个参数必须是异常类型
                args[0] = ex;
            }
        }

        // 存在 ProceedingJoinPoint， @Around 通知的调用
        if (pjp != null) {
            if (parameterCount == 0) {
                throw new IllegalStateException("环绕通知的参数中缺少 ProceedingJoinPoint");
            } else {
                args[0] = pjp;
            }
        }
        return invokeAdviceMethod(args);
    }

    /**
     * AfterThrowing 异常回显
     * 环绕通知会用到通知方法的返回值，其他通知用不到
     *
     * @param args
     * @return
     */
    private Object invokeAdviceMethod(Object[] args) throws Throwable {
        try {
            return this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), args);
        } catch (IllegalAccessException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    protected void setThrowingName(String name) {
        if (name.equals("ex")) {
            this.throwingName = name;
            // 下面的方法不行，因为编译后的 class 没有参数名信息，变成 arg0、arg1 这种
            /*Parameter[] parameters = this.aspectJAdviceMethod.getParameters();
            for (Parameter parameter : parameters) {
                // 找到要拦截的异常类型
                if (parameter.getName().equals(this.throwingName)) {
                    this.discoveredThrowingType = parameter.getType();
                    return;
                }
            }*/
            // 规定如果 @AfterThrowing 的 throwing 参数设置为 ex，那么参数列表第 0 个必须是异常类
            Class<?> exClass = null;
            try {
                exClass = this.aspectJAdviceMethod.getParameterTypes()[0];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("方法中缺少异常参数。method = " + this.aspectJAdviceMethod);
            }

            if (Throwable.class.isAssignableFrom(exClass)) {
                this.discoveredThrowingType = exClass;
            } else {
                throw new IllegalArgumentException("方法中缺少异常参数，找不到要拦截的异常类型。method = " + this.aspectJAdviceMethod);
            }
        } else if (name.length() > 0) {
            this.throwingName = name;
            try {
                this.discoveredThrowingType = Class.forName(name);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Throwing name '" + name +
                        "' is neither a valid argument name nor the fully-qualified " +
                        "name of a Java type on the classpath. Root cause: " + ex);
            }
        } else {
            // throwing 没提供，默认拦截索引异常
        }
    }

    protected Class<?> getDiscoveredThrowingType() {
        return this.discoveredThrowingType;
    }

}
