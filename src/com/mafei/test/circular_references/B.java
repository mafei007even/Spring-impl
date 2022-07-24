package com.mafei.test.circular_references;

import com.mafei.spring.anno.Autowired;
import com.mafei.spring.anno.Component;
import com.mafei.spring.anno.Lazy;
import com.mafei.spring.anno.Scope;
import com.mafei.spring.interfaces.InitializingBean;

/**
 * @author mafei007
 * @date 2022/7/23 16:29
 */
// @Scope("prototype")
@Component
public class B implements MyInterface, InitializingBean {

    /**
     * 采用 jdk 动态代理，注入的类型需要是接口类型
     */
    // @Autowired
    private MyInterface a;

    public B(@Lazy MyInterface a) {
        this.a = a;
        System.out.println("B 创建，构造注入的 a 为：" + a.getClass());
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("B 初始化");
    }


    @Override
    public void foo() {
        System.out.println("B.foo");
    }

    // @Autowired
    public void setA(MyInterface a) {
        System.out.println("B 😋😋😋😋 依赖注入 setA(" + a.getClass().getName() + ")");
        this.a = a;
    }
}
