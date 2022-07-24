package com.mafei.spring.aop.proxy;

import com.mafei.spring.MaFeiApplicationContext;

/**
 * 用于构造方法注入时的延迟注入
 * 有可能注入的是个多例 bean，所以每次都从容器中获取，不能缓存起来
 *
 * @author mafei007
 * @date 2022/7/24 18:13
 */
public class LazyInjectTargetSource implements TargetSource {

    private final MaFeiApplicationContext applicationContext;
    private final String beanName;

    public LazyInjectTargetSource(MaFeiApplicationContext applicationContext, String beanName) {
        this.applicationContext = applicationContext;
        this.beanName = beanName;
    }

    @Override
    public Object getTarget() throws Exception {
        return applicationContext.getBean(beanName);
    }
}
