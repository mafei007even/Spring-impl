package com.mafei.spring.aop;

import com.mafei.spring.aop.advisor.Advisor;
import com.mafei.spring.aop.advisor.AspectJExpressionPointcut;
import com.mafei.spring.aop.advisor.DefaultPointcutAdvisor;
import com.mafei.spring.aop.advisor.joinpoint.ProceedingJoinPoint;
import com.mafei.spring.aop.anno.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mafei007
 * @date 2022/7/7 21:38
 */
public class DefaultAspectJAdvisorFactory implements AspectJAdvisorFactory {
    @Override
    public boolean isAspect(Class<?> clazz) {
        return clazz.isAnnotationPresent(Aspect.class);
    }

    @Override
    public List<Advisor> getAdvisors(Class<?> clazz) {
        PrototypeAspectInstanceFactory aspectInstanceFactory = new PrototypeAspectInstanceFactory(clazz);
        // 高级切面转低级切面类
        List<Advisor> list = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                // 切点 pointcut
                String expression = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                BeforeAdvice advice = new BeforeAdvice(method, aspectInstanceFactory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            } else if (method.isAnnotationPresent(After.class)) {
                // 切点 pointcut
                String expression = method.getAnnotation(After.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                AfterAdvice advice = new AfterAdvice(method, aspectInstanceFactory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            } else if (method.isAnnotationPresent(AfterReturning.class)) {
                // 切点 pointcut
                String expression = method.getAnnotation(AfterReturning.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                AfterReturningAdvice advice = new AfterReturningAdvice(method, aspectInstanceFactory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            } else if (method.isAnnotationPresent(AfterThrowing.class)) {
                // 切点 pointcut
                AfterThrowing afterThrowing = method.getAnnotation(AfterThrowing.class);
                String expression = afterThrowing.value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                AfterThrowingAdvice advice = new AfterThrowingAdvice(method, aspectInstanceFactory);
                advice.setThrowingName(afterThrowing.throwing());
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            } else if (method.isAnnotationPresent(Around.class)) {
                if (method.getParameterCount() == 0) {
                    throw new IllegalStateException("环绕通知的参数中缺少 ProceedingJoinPoint");
                }
                if (!method.getParameterTypes()[0].equals(ProceedingJoinPoint.class)) {
                    throw new IllegalStateException("环绕通知的参数中第一个位置必须是 ProceedingJoinPoint");
                }
                // 切点 pointcut
                Around around = method.getAnnotation(Around.class);
                String expression = around.value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                AroundAdvice advice = new AroundAdvice(method, aspectInstanceFactory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
        }
        return list;
    }
}
