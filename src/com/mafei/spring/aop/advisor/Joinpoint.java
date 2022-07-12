package com.mafei.spring.aop.advisor;

/**
 * 连接点，是程序执行的一个点。例如，一个方法的执行或者一个异常的处理。
 * 在 Spring AOP 中，一个连接点总是代表一个方法执行。
 * @author mafei007
 * @date 2022/7/7 22:47
 */
public interface Joinpoint {

    Object proceed() throws Throwable;

}
