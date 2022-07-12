package com.mafei.spring.aop.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author mafei007
 * @date 2022/7/8 01:57
 */
public class AopUtils {
    public static Object invokeJoinpointUsingReflection(Object target, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}
