package com.mafei.spring.interfaces;

/**
 * @author mafei007
 * @date 2022/7/29 19:03
 */
public interface DisposableBean {
    void destroy() throws Exception;
}
