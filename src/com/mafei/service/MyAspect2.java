package com.mafei.service;

import com.mafei.spring.anno.Component;
import com.mafei.spring.aop.anno.After;
import com.mafei.spring.aop.anno.AfterReturning;
import com.mafei.spring.aop.anno.Aspect;
import com.mafei.spring.aop.anno.Before;

/**
 * @author mafei007
 * @date 2022/7/8 03:52
 */
@Aspect
@Component
public class MyAspect2 {

    @Before("execution(* *.PostService.*())")
    public void f3() {
        System.out.println("before 2 通知....");
    }


    @AfterReturning("execution(* *.PostService.*())")
    public void f4() {
        System.out.println("AfterReturning 1 通知....");
    }


    @AfterReturning("execution(* *.PostService.*())")
    public void f5() {
        System.out.println("AfterReturning 2 通知....");
    }

    @Before("execution(* *.PostService.*())")
    public void f1() {
        System.out.println("before 1 通知....");
    }

    @After("execution(* *.PostService.*())")
    public void f2() {
        System.out.println("after 通知....");
    }



    @After("execution(* *.UserService.*())")
    public void f11() {
        System.out.println("after 通知 in Aspect2 ....");
    }


}
