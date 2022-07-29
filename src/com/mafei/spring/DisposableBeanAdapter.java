package com.mafei.spring;

import com.mafei.spring.interfaces.DisposableBean;

/**
 * 适配器，适配以执行各种形式的销毁方法
 *
 * @author mafei007
 * @date 2022/7/29 19:10
 */
public class DisposableBeanAdapter implements DisposableBean {

    private Object bean;
    private String beanName;
    private BeanDefinition beanDefinition;

    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
    }

    public static boolean hasDestroyMethod(Object bean, BeanDefinition beanDefinition) {
        if (bean instanceof DisposableBean || bean instanceof AutoCloseable) {
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        try {
            if (bean instanceof DisposableBean) {
                ((DisposableBean) bean).destroy();
            } else if (bean instanceof AutoCloseable) {
                ((AutoCloseable) bean).close();
            }
        } catch (Exception e) {
            System.out.println("Invocation of destroy method failed on bean with name '" + this.beanName + "'");
        }
    }
}
