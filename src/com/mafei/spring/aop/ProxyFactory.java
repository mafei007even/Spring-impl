package com.mafei.spring.aop;

import com.mafei.spring.aop.advisor.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 实际上就是个 ProxyConfig，每个代理对象都持有一个 ProxyFactory
 * 一个 ProxyFactory 只能生产一个代理对象
 *
 * @author mafei007
 * @date 2022/7/8 00:41
 */
public class ProxyFactory {

    private List<Advisor> advisorList;
    private Object target;
    private Class<?> targetClass;
    private boolean proxyTargetClass;

    public ProxyFactory() {
        this.proxyTargetClass = false;
        this.advisorList = new ArrayList<>();
    }

    public void addAdvisors(List<Advisor> advisorList) {
        this.advisorList.addAll(advisorList);
    }

    public Object getProxy() {
        AopProxy aopProxy = createAopProxy();
        Object proxy = aopProxy.getProxy();
        // System.out.println("创建了代理：" + proxy);
        return proxy;
    }

    public AopProxy createAopProxy() {
        if (isProxyTargetClass()) {
            return new ObjenesisCglibAopProxy(this);
        } else {
            // 有接口
            if (getTargetClass().getInterfaces().length > 0) {
                return new JdkDynamicAopProxy(this);
            } else {
                // 没接口
                return new ObjenesisCglibAopProxy(this);
            }
        }
    }

    /**
     * TODO 没有实现动态通知调用
     * 得到此 method 的拦截器链，就是一堆环绕通知
     * 需要根据 invoke 的 method 来做进一步确定，过滤出应用在这个 method 上的 Advice
     *
     * @param method
     * @param targetClass
     * @return
     */
    public List<Interceptor> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        List<Interceptor> interceptorList = new ArrayList<>(this.advisorList.size());
        for (Advisor advisor : this.advisorList) {
            MethodMatcher methodMatcher = advisor.getPointcut().getMethodMatcher();
            // 切点表达式匹配才添加此 MethodInterceptor
            if (methodMatcher.matches(method, targetClass)) {
                Advice advice = advisor.getAdvice();
                if (advice instanceof MethodInterceptor) {
                    interceptorList.add((MethodInterceptor) advice);
                }
            }
        }
        return interceptorList;
    }

    public void setTarget(Object bean) {
        this.target = bean;
        this.targetClass = bean.getClass();
    }

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }


    public Object getTarget() {
        return target;
    }
}
