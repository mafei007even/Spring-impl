package com.mafei.spring.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加在构造方法参数上面，
 * 对加了 @Lazy 注解的依赖生成代理对象，推迟 bean 的获取，
 * 解决构造方法的循环依赖问题
 *
 * @author mafei007
 * @date 2022/7/24 19:20
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lazy {
}
