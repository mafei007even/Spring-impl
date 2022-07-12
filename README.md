# Spring-impl
学习了 Spring 原理后写的一个 Spring 简单实现。

主要实现了 IOC 和 AOP 功能

## 1. IOC
IOC 实现的很粗略，完成了 @ComponentScan 组件扫描、简单的 Bean 生命周期（创建、依赖注入、初始化、各种 Aware 接口回调）、Bean后处理器的调用（BeanPostProcessor）。
容器类的实现是 `com.mafei.spring.MaFeiApplicationContext`

## 2. AOP
AOP 是此项目中主要的功能模块，在学习了 AOP 的整体流程后，参考 Spring 源码实现，绝大部分类名都和 Spring 中的类名一致。
对 AOP 中的主要接口做了抽象，方便扩展，简单起见，和 Spring 的接口设计略有不同。

完成了 5 种通知类型`（@Before、@AfterReturning、@After、@AfterThrowing、@Around）`的解析，对符合切点的目标对象进行代理增强。
应用在目标方法上的多个通知会链式调用执行，且实现了通知的调用顺序控制（对 Advisor 切面排序）。

切点表达式的解析使用 `((String)(expression)).contains(className)` 进行简单地判断，判断通过即代理类中的所有方法，没有针对具体的某些方法做解析。

AOP 功能的实现在 `com.mafei.spring.aop` 包下，通过 `com.mafei.spring.aop.AnnotationAwareAspectJAutoProxyCreator` Bean 后处理器对符合切点的目标对象进行代理增强。

## 功能测试
在 `com.mafei.service` 包下