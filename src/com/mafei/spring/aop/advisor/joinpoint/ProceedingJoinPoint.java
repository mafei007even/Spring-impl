package com.mafei.spring.aop.advisor.joinpoint;

/**
 * 就是一个 MethodInvocation，
 * Spring 的实现 MethodInvocationProceedingJoinPoint 中就是内置了一个 MethodInvocation
 * @author mafei007
 * @date 2022/7/8 22:48
 */
public interface ProceedingJoinPoint extends JoinPoint {

    /**
     * 不带参数调用proceed()将导致调用者的原始参数在调用时提供给底层方法
     *
     * @return
     * @throws Throwable
     */
    Object proceed() throws Throwable;

    /**
     * 重载方法，它接受参数数组 (Object[] args)。调用时，数组中的值将用作底层方法的参数。
     * 可以修改实参
     *
     * @param args
     * @return
     * @throws Throwable
     */
    Object proceed(Object[] args) throws Throwable;
}
