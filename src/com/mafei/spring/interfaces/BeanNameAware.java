package com.mafei.spring.interfaces;

/**
 * 将 beanName 传递给 bean
 * 某个bean 实现了这个接口，就能得到它的 beanName
 * 由 Spring 调用
 *
 * @author mafei007
 * @date 2022/6/29 23:10
 */
public interface BeanNameAware {

    void setBeanName(String beanName);

}
