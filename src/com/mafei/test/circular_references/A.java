package com.mafei.test.circular_references;

import com.mafei.spring.anno.Autowired;
import com.mafei.spring.anno.Component;
import com.mafei.spring.anno.Lazy;
import com.mafei.spring.anno.Scope;
import com.mafei.spring.interfaces.InitializingBean;
import com.mafei.spring.interfaces.ObjectFactory;

/**
 * @author mafei007
 * @date 2022/7/23 16:27
 */
// @Scope("prototype")
@Component
public class A implements MyInterface, InitializingBean {

    /**
     * 采用 jdk 动态代理，注入的类型需要是接口类型
     */
    // @Autowired
    private MyInterface b;

    private ObjectFactory<MyInterface> bObj;

    public A(@Lazy MyInterface b) {
    // public A(ObjectFactory<MyInterface> b) {
        this.b = b;
        // this.bObj = b;
        System.out.println("A 创建，构造注入的 b 为：" + b.getClass());
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("A 初始化");
    }


    @Override
    public void foo() {
        System.out.println("【【【【【【【【【【【【【【【【【A.foo start】......." + this.b.getClass());
        this.b.foo();
        // this.bObj.getObject().foo();
        System.out.println("【【【【【【【【【【【【【【【【【A.foo end】");
    }

    // @Autowired
    public void setB(MyInterface b) {
        System.out.println("A 😋😋😋😋 依赖注入 setB(" + b.getClass().getName() + ")");
        this.b = b;
    }

}
