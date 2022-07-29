package com.mafei.test.circular_references;

import com.mafei.spring.anno.Autowired;
import com.mafei.spring.anno.Component;
import com.mafei.spring.anno.Lazy;
import com.mafei.spring.anno.Scope;
import com.mafei.spring.interfaces.InitializingBean;

/**
 * @author mafei007
 * @date 2022/7/25 03:07
 */
// @Scope("prototype")
// @Component
public class D implements MyInterface, InitializingBean {

    /**
     * 采用 jdk 动态代理，注入的类型需要是接口类型
     */
    // @Autowired
    private MyInterface c;

    public D(MyInterface c) {
        this.c = c;
        System.out.println("D 创建，构造注入的 c 为：" + c.getClass());
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("D 初始化");
    }

    @Override
    public void foo() {
        System.out.println("【【【【【【【【【【【【【【【【【D.foo start】......." + this.c.getClass());
        // this.c.foo();
        System.out.println("【【【【【【【【【【【【【【【【【D.foo end】");
    }

    @Autowired
    public void setC(MyInterface c) {
        System.out.println("D 😋😋😋😋 依赖注入 setC(" + c.getClass().getName() + ")");
        this.c = c;
    }

}
