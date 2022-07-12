package com.mafei.spring.aop.advisor.joinpoint;

import com.mafei.spring.aop.advisor.MethodInvocation;

/**
 * @author mafei007
 * @date 2022/7/8 22:58
 */
public class MethodInvocationProceedingJoinPoint implements ProceedingJoinPoint {

    private final MethodInvocation methodInvocation;

    public MethodInvocationProceedingJoinPoint(MethodInvocation mi) {
        this.methodInvocation = mi;
    }

    @Override
    public Object proceed() throws Throwable {
        return this.methodInvocation.proceed();
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        this.methodInvocation.setArguments(args);
        return this.methodInvocation.proceed();
    }

    @Override
    public Object[] getArgs() {
        return this.methodInvocation.getArguments().clone();
    }

    @Override
    public String getMethodName() {
        return this.methodInvocation.getMethod().getName();
    }
}
