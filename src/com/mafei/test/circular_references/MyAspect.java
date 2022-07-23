package com.mafei.test.circular_references;

import com.mafei.spring.anno.Component;
import com.mafei.spring.aop.anno.After;
import com.mafei.spring.aop.anno.Aspect;
import com.mafei.spring.aop.anno.Before;

/**
 * @author mafei007
 * @date 2022/7/24 00:07
 */
@Aspect
@Component
public class MyAspect {

    @Before("execution(* *.(A|B).*())")
    public void before01() {
        System.out.println("before01...");
    }

    @After("execution(* *.(A|B).*())")
    public void after01() {
        System.out.println("after01...");
    }

}
