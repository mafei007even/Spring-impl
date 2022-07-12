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
public class AfterAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public AfterAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    /*
    	@Override
	@Nullable
	public Object invoke(MethodInvocation mi) throws Throwable {
		try {
			return mi.proceed();
		}
		finally {
			invokeAdviceMethod(getJoinPointMatch(), null, null);
		}
	}
     */

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } finally {
            after();
        }
    }

    public void after() throws Throwable {
        invokeAdviceMethod(null,null);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
