package com.mafei.spring.aop.proxy;

/**
 * @author mafei007
 * @date 2022/7/24 18:09
 */
public class SingletonTargetSource implements TargetSource {

    private final Object target;

    public SingletonTargetSource(Object target) {
        this.target = target;
    }

    @Override
    public Object getTarget() throws Exception {
        return this.target;
    }
}
