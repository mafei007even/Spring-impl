# Spring-impl
学习了 Spring 原理后写的一个 Spring 简单实现。

主要实现了 IOC 和 AOP 功能

## IOC
#### Bean 生命周期
完成了 @ComponentScan 组件扫描、Bean 生命周期`（创建、依赖注入、初始化）`， 
包括：Bean后处理器（BeanPostProcessor）的调用、各种 Aware 接口回调。

依赖注入使用 @Autowired 从容器中 byName 查找依赖。

#### 循环依赖
- 使用三级缓存解决「属性注入和 set 方法注入」的循环依赖问题，也解决了涉及注入代理对象的循环依赖问题。
- 使用 @Lazy 注解、ObjectFactory 解决构造方法注入的循环依赖问题
> 循环依赖的两个 bean 都是多例的 bean，无法解决。 
> 
> 必须至少其中一个是单例 bean，才能解决循环依赖问题

#### 简化的容器 refresh 流程：
1. 收集 BeanDefinition，根据 @ComponentScan 的包路径扫描加了 @Component 注解的类，放入 beanDefinitionMap
2. 向容器中注册基本的 Bean 后处理器（BeanPostProcessor）BeanDefinition，并创建 beanDefinitionMap 中所有的 Bean 后处理器，放入容器 singletonObjects 中，添加到 beanPostProcessorList
3. 初始化所有单例 bean

容器类的实现是 `com.mafei.spring.MaFeiApplicationContext`

## AOP
完成了 5 种通知类型`（@Before、@AfterReturning、@After、@AfterThrowing、@Around）`的解析，对符合切点的目标对象进行代理增强。
应用在目标方法上的多个通知会链式调用执行，且实现了通知的调用顺序控制（对 Advisor 切面排序）。

切点表达式的解析使用 `((String)(expression)).contains(className)` 进行简单地判断，判断通过表示此切面可以应用在这个 class 上，能对这个 class 中所有方法进行增强。

AOP 功能的实现在 `com.mafei.spring.aop` 包下，通过 `com.mafei.spring.aop.AnnotationAwareAspectJAutoProxyCreator` Bean 后处理器对符合切点的目标对象进行代理增强。

可以使用 AopContext 获取当前正在运行的 AOP 代理对象

参考 Spring 源码实现，大部分类名都和 Spring 中的类名一致。
对 AOP 中的主要接口做了抽象，方便扩展。简单起见，和 Spring 的接口设计略有不同。

## 功能测试 Demo
在 `com.mafei.test` 包下