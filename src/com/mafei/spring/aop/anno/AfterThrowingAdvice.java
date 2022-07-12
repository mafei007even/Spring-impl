package com.mafei.spring.aop.anno;

import com.mafei.spring.aop.AspectInstanceFactory;
import com.mafei.spring.aop.advisor.Advice;
import com.mafei.spring.aop.advisor.CommonAdvice;
import com.mafei.spring.aop.advisor.MethodInterceptor;
import com.mafei.spring.aop.advisor.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author mafei007
 * @date 2022/7/7 23:23
 */
public class AfterThrowingAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public AfterThrowingAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    @Override
    public void setThrowingName(String name) {
        super.setThrowingName(name);
    }

    /*
     public Object invoke(MethodInvocation mi) throws Throwable {
		try {
			return mi.proceed();
		}
		catch (Throwable ex) {
			if (shouldInvokeOnThrowing(ex)) {
				invokeAdviceMethod(getJoinPointMatch(), null, ex);
			}
			throw ex;
		}
	}
     */

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable ex) {
            if (shouldInvokeOnThrowing(ex)) {
                afterThrowing(ex);
            }
            throw ex;
        }
    }

    /**
     * 只有当抛出的异常是给定抛出类型的子类型时，才会调用 afterThrowing 通知。
     *
     * @param ex
     * @return
     */
    private boolean shouldInvokeOnThrowing(Throwable ex) {
        return getDiscoveredThrowingType().isAssignableFrom(ex.getClass());
    }

    public void afterThrowing(Throwable ex) throws Throwable {
        invokeAdviceMethod(null, ex);
    }

    @Override
    public int getOrder() {
        return 2;
    }

}
