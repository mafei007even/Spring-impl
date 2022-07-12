package com.mafei.spring.aop.advisor;

/**
 * 环绕通知
 * @author mafei007
 * @date 2022/7/7 22:45
 */
public interface MethodInterceptor extends Interceptor {

    Object invoke(MethodInvocation invocation) throws Throwable;
}
