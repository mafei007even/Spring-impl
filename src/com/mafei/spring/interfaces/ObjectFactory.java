package com.mafei.spring.interfaces;

/**
 * @author mafei007
 * @date 2022/7/23 16:52
 */
@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws RuntimeException;
}
