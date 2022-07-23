package com.mafei.test.circular_references;

import com.mafei.spring.anno.Autowired;
import com.mafei.spring.anno.Component;
import com.mafei.spring.interfaces.InitializingBean;

/**
 * @author mafei007
 * @date 2022/7/23 16:29
 */
@Component
public class B implements MyInterface, InitializingBean {

    /**
     * 采用 jdk 动态代理，注入的类型需要是接口类型
     */
    @Autowired
    private MyInterface a;

    public B() {
        System.out.println("B 创建");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("B 初始化");
    }


    @Override
    public void foo() {
        System.out.println("B.foo");
    }

}
