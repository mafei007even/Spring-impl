package com.mafei.spring.aop.proxy;

/**
 * 获取当前正在运行的 AOP 代理对象
 *
 * @author mafei007
 * @date 2022/7/24 23:39
 */
public final class AopContext {

    public static final ThreadLocal<Object> currentProxy = new ThreadLocal<>();

    private AopContext() {
    }

    public static Object currentProxy() {
        Object proxy = currentProxy.get();
        if (proxy == null) {
            throw new IllegalStateException("当前没有代理在运行");
        }
        return proxy;
    }

    public static Object setCurrentProxy(Object proxy) {
        Object old = currentProxy.get();
        if (proxy != null) {
            currentProxy.set(proxy);
        } else {
            currentProxy.remove();
        }
        return old;
    }

}
