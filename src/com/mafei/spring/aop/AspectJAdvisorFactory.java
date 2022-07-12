package com.mafei.spring.aop;

import com.mafei.spring.aop.advisor.Advisor;

import java.util.List;

/**
 * 解析 @Aspect 切面类中的所有切面
 * @author mafei007
 * @date 2022/7/7 21:32
 */
public interface AspectJAdvisorFactory {

    /**
     * 是否是切面类 @Aspect
     * @param clazz
     * @return
     */
    boolean isAspect(Class<?> clazz);

    /**
     * 解析 @Aspect 切面类中的所有切面
     * @param clazz
     * @return
     */
    List<Advisor> getAdvisors(Class<?> clazz);

}
