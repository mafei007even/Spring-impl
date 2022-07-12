package com.mafei.spring.aop;

import com.mafei.spring.aop.advisor.DefaultMethodInvocation;
import com.mafei.spring.aop.advisor.Interceptor;
import com.mafei.spring.aop.util.AopUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author mafei007
 * @date 2022/7/8 01:16
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private ProxyFactory proxyFactory;

    public JdkDynamicAopProxy(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                proxyFactory.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 得到此 method 的拦截器链，就是一堆环绕通知
        // 需要根据 invoke 的 method 来做进一步确定，过滤出应用在这个 method 上的 Advice
        List<Interceptor> chain = this.proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(method, this.proxyFactory.getTargetClass());
        Object target = this.proxyFactory.getTarget();
        if (chain.isEmpty()) {
            return AopUtils.invokeJoinpointUsingReflection(target, method, args);
        } else {
            DefaultMethodInvocation methodInvocation = new DefaultMethodInvocation(target, method, args, chain);
            Object retVal = methodInvocation.proceed();
            return retVal;
        }
    }
}
