package com.mafei.spring.aop.proxy;

/**
 * 用于获取AOP调用的当前“目标”
 *
 * @author mafei007
 * @date 2022/7/24 18:05
 */
public interface TargetSource {

    Object getTarget() throws Exception;

}
