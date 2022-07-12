package com.mafei.service;

import com.mafei.spring.anno.Component;
import com.mafei.spring.aop.advisor.joinpoint.ProceedingJoinPoint;
import com.mafei.spring.aop.anno.*;

/**
 * @author mafei007
 * @date 2022/7/8 02:43
 */
@Aspect
@Component
public class MyAspect {

    @Around("execution(* *.UserService.*())")
    public Object f5(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        String methodName = pjp.getMethodName();
        if (methodName.equals("test")) {
            args[0] = 200;
        }
        Object retVal = null;
        try {
            System.out.println("环绕通知 before ...");
            // 调用执行链
            retVal = pjp.proceed(args);
            System.out.println("环绕通知 after returning ...");
            return retVal;
        } catch (Throwable e) {
            System.out.println("环绕通知 after throwing...\uD83D\uDE21\uD83D\uDE21" + e);
            throw new RuntimeException(e);
        } finally {
            System.out.println("环绕通知 after ...");
        }
    }


    @Before("execution(* *.UserService.*())")
    public void f1() {
        System.out.println("before 1 通知....");
    }

    @After("execution(* *.UserService.*())")
    public void f2() {
        System.out.println("after 通知....");
    }


    // @Before("execution(* *.UserService.*())")
    // public void f3() {
    //     System.out.println("before 2 通知....");
    // }


    @AfterReturning("execution(* *.UserService.*())")
    public void f4() {
        System.out.println("AfterReturning 1 通知....");
    }


    // @AfterReturning("execution(* *.UserService.*())")
    // public void f5() {
    //     System.out.println("AfterReturning 2 通知....");
    // }

    @AfterThrowing(value = "execution(* *.UserService.*())", throwing = "ex")
    public void throwing1(ArithmeticException ex) {
        System.out.println("\uD83D\uDE21\uD83D\uDE21\uD83D\uDE21 throwing....拦截算术异常：" + ex);
    }

    @AfterThrowing(value = "execution(* *.UserService.*())")
    public void throwing2() {
        System.out.println("\uD83D\uDE21\uD83D\uDE21\uD83D\uDE21 throwing....出异常了, @AfterThrowing 拦截了这个全局异常");
    }

    @AfterThrowing(value = "execution(* *.UserService.*())", throwing = "java.lang.NullPointerException")
    public void throwing3(NullPointerException ex) {
        System.out.println("\uD83D\uDE21\uD83D\uDE21\uD83D\uDE21 throwing....拦截空指针异常：" + ex);
    }


    @AfterThrowing(value = "execution(* *.UserService.*())", throwing = "ex")
    public void throwing4(Throwable e) {
        System.out.println("\uD83D\uDE21\uD83D\uDE21\uD83D\uDE21 throwing....拦截 Throwable：" + e);
    }
}
