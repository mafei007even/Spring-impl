package com.mafei.spring.interfaces;

/**
 * 在 bean 初始化前后对其进行一些操作，使程序员可以干涉 bean 初始化的过程
 * <p></p>
 * 可以对 bean 进行功能扩展、增强
 * <p></p>
 * AOP 就是在这里进行
 * AOP ： 返回一个代理对象
 * @author mafei007
 * @date 2022/6/29 23:23
 */
public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

}
