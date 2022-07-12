package com.mafei.spring.aop.advisor;

/**
 * Core Spring pointcut abstraction.
 * 切点使用一个 MethodMatcher 对象来判断某个方法是否有资格用于切面
 *
 * @author mafei007
 * @date 2022/7/7 22:11
 */
public interface Pointcut {

    /**
     * Return the MethodMatcher for this pointcut.
     * @return the MethodMatcher (never {@code null})
     */
    MethodMatcher getMethodMatcher();

}
