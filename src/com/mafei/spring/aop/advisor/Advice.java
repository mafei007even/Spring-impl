package com.mafei.spring.aop.advisor;

import com.mafei.spring.core.Ordered;

/**
 * 通知
 * Spring 中此接口并没有实现 Ordered，而是使用别的方法进行排序
 *
 * @author mafei007
 * @date 2022/7/7 20:59
 */
public interface Advice extends Ordered {
}
