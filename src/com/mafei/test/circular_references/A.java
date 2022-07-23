package com.mafei.test.circular_references;

import com.mafei.spring.anno.Autowired;
import com.mafei.spring.anno.Component;
import com.mafei.spring.interfaces.InitializingBean;

/**
 * @author mafei007
 * @date 2022/7/23 16:27
 */
@Component
public class A implements MyInterface, InitializingBean {

    /**
     * 采用 jdk 动态代理，注入的类型需要是接口类型
     */
    // @Autowired
    private MyInterface b;

    public A() {
        System.out.println("A 创建");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("A 初始化");
    }


    @Override
    public void foo() {
        System.out.println("A.foo");
    }

    @Autowired
    public void setB(MyInterface b) {
        System.out.println("A 😋😋😋😋 依赖注入 setB(" + b.getClass().getName() + ")");
        this.b = b;
    }
}
