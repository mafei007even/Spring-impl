package com.mafei.spring.aop;

/**
 * 提供调用切面方法的类
 * 每次返回新的切面对象
 * @author mafei007
 * @date 2022/7/8 00:04
 */
public class PrototypeAspectInstanceFactory implements AspectInstanceFactory{

    private Class<?> clazz;

    public PrototypeAspectInstanceFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object getAspectInstance() {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
