package com.mafei.test.circular_references;

import com.mafei.spring.MaFeiApplicationContext;

/**
 * @author mafei007
 * @date 2022/7/23 16:25
 */
public class Test {

    public static void main(String[] args) {
        MaFeiApplicationContext applicationContext = new MaFeiApplicationContext(AppConfig.class);
        MyInterface a = applicationContext.getBean("a", MyInterface.class);
        a.foo();
        MyInterface b = applicationContext.getBean("b", MyInterface.class);
        b.foo();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // MyInterface c = applicationContext.getBean("c", MyInterface.class);
        // c.foo();
        applicationContext.close();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // applicationContext.destroyBean(a);
        // applicationContext.destroyBean(b);
    }

}
