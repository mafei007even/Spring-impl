package com.mafei.spring.aop.advisor;

import com.mafei.spring.core.Ordered;

/**
 * 切面
 * Spring 中此接口并没有实现 Ordered，而是使用别的方法进行排序
 *
 * @author mafei007
 * @date 2022/7/7 20:58
 */
public interface Advisor extends Ordered {

    /**
     * 此方法应该再封装一个接口：PointcutAdvisor，放在这个接口里，这里直接放在 Advisor 接口 里了
     * @return
     */
    Pointcut getPointcut();

    Advice getAdvice();
}
