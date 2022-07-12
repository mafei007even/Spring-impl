package com.mafei.spring.aop.anno;

import com.mafei.spring.aop.AspectInstanceFactory;
import com.mafei.spring.aop.advisor.Advice;
import com.mafei.spring.aop.advisor.CommonAdvice;
import com.mafei.spring.aop.advisor.MethodInterceptor;
import com.mafei.spring.aop.advisor.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author mafei007
 * @date 2022/7/7 23:27
 */
public class AfterReturningAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public AfterReturningAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    /*
    	@Override
	@Nullable
	public Object invoke(MethodInvocation mi) throws Throwable {
		Object retVal = mi.proceed();
		this.advice.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
		return retVal;
	}

     */

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        afterReturning();
        return retVal;
    }

    public void afterReturning() throws Throwable {
        invokeAdviceMethod(null,null);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
