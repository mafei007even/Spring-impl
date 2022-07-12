package com.mafei.spring.aop.advisor;

import com.mafei.spring.aop.advisor.MethodMatcher;
import com.mafei.spring.aop.advisor.Pointcut;

import java.lang.reflect.Method;

/**
 * 既是 Pointcut，又是 MethodMatcher
 * @author mafei007
 * @date 2022/7/7 22:14
 */
public class AspectJExpressionPointcut implements Pointcut, MethodMatcher {

    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (expression.startsWith("execution")) {
            return executionMatches(method, targetClass);
        } else if (expression.startsWith("@annotation")) {
            return annotationMatches(method, targetClass);
        } else {
            System.out.println("未知 expression，默认返回 true");
            return true;
        }
    }

    /**
     * 解析这种："@annotation(org.springframework.transaction.annotation.Transactional)"
     * 判断方法上是否有 expression 表达式里的注解就行了
     *
     * "@annotation(org.springframework.web.bind.annotation.GetMapping)"
     * 所有被GetMapping注解修饰的方法会织入advice
     * @param method
     * @param targetClass
     * @return
     */
    private boolean annotationMatches(Method method, Class<?> targetClass) {
        return true;
    }

    private boolean executionMatches(Method method, Class<?> targetClass) {
        String simpleName = targetClass.getSimpleName();
        if (expression.contains(simpleName)) {
            return true;
        }
        return false;
    }
}
