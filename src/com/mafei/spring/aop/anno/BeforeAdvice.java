package com.mafei.spring.aop.anno;

import com.mafei.spring.aop.AspectInstanceFactory;
import com.mafei.spring.aop.advisor.Advice;
import com.mafei.spring.aop.advisor.CommonAdvice;
import com.mafei.spring.aop.advisor.MethodInterceptor;
import com.mafei.spring.aop.advisor.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author mafei007
 * @date 2022/7/7 23:00
 */
public class BeforeAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public BeforeAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    /*
    	public Object invoke(MethodInvocation mi) throws Throwable {
		this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());
		return mi.proceed();
	}
     */

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        before();
        return invocation.proceed();
    }

    public void before () throws Throwable {
        invokeAdviceMethod(null,null);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
