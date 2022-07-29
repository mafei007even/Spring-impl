# Spring-impl
学习了 Spring 原理后写的一个 Spring 简单实现。

主要实现了 IOC 和 AOP 功能

## IOC
### Bean 生命周期
完成了 [@ComponentScan](src/com/mafei/spring/anno/ComponentScan.java) 组件扫描、Bean 生命周期`（创建、依赖注入、初始化、销毁）`， 
包括：Bean后处理器 [BeanPostProcessor](src/com/mafei/spring/interfaces/BeanPostProcessor.java) 的调用、各种 Aware 接口回调。

依赖注入使用 @Autowired 从容器中 byName 查找依赖。

### 循环依赖
- 使用三级缓存解决「属性注入和 set 方法注入」的循环依赖问题，也解决了涉及注入代理对象的循环依赖问题。
- 使用 @Lazy 注解、[ObjectFactory](src/com/mafei/spring/interfaces/ObjectFactory.java) 解决构造方法注入的循环依赖问题
> 两边相互依赖的 bean 必须至少其中一个是单例 bean，才能解决循环依赖问题。采用构造方法注入，单例、多例都行。
> 
> 做了循环引用检测，发现循环引用时抛出异常，而不是无限递归爆栈。
> 
> 可能会抛出异常的原因：
> - 使用「属性注入或 set 方法注入」两边相互依赖的 bean 都是多例 bean
> - 使用构造方法注入没加 @Lazy、ObjectFactory 推迟注入 bean

### 简化的容器 refresh 流程
1. 收集 BeanDefinition，根据 @ComponentScan 的包路径扫描加了 @Component 注解的类，放入 beanDefinitionMap
2. 向容器中注册基本的 Bean 后处理器（BeanPostProcessor）BeanDefinition，并创建 beanDefinitionMap 中所有的 Bean 后处理器，放入容器 singletonObjects 中，添加到 beanPostProcessorList
3. 初始化所有单例 bean

容器类的实现是 [MaFeiApplicationContext](src/com/mafei/spring/MaFeiApplicationContext.java)

## AOP
完成了 5 种通知类型（[@Before](src/com/mafei/spring/aop/anno/Before.java)、[@AfterReturning](src/com/mafei/spring/aop/anno/AfterReturning.java)、[@After](src/com/mafei/spring/aop/anno/After.java)、[@AfterThrowing](src/com/mafei/spring/aop/anno/AfterThrowing.java)、[@Around](src/com/mafei/spring/aop/anno/Around.java)）的解析，对符合切点的目标对象进行代理增强。
应用在目标方法上的多个通知会链式调用执行，且实现了通知的调用顺序控制（对 [Advisor](src/com/mafei/spring/aop/advisor/Advisor.java) 切面排序）。

切点表达式的解析使用 `((String)(expression)).contains(className)` 进行简单地判断，判断通过表示此切面可以应用在这个 class 上，能对这个 class 中所有方法进行增强。

AOP 功能的实现在 `com.mafei.spring.aop` 包下，通过 Bean 后处理器 [AnnotationAwareAspectJAutoProxyCreator](src/com/mafei/spring/aop/AnnotationAwareAspectJAutoProxyCreator.java) 对符合切点的目标对象进行代理增强。
如果发生了循环依赖要在依赖注入阶段提前创建代理，此 Bean 后处理器使用缓存避免了代理对象的重复创建。

可以使用 [AopContext](src/com/mafei/spring/aop/proxy/AopContext.java) 获取当前线程正在运行的 AOP 代理对象。

参考 Spring 源码实现，大部分类名都和 Spring 中的类名一致。
对 AOP 中的主要接口做了抽象，方便扩展。简单起见，和 Spring 的接口设计略有不同。

## 使用到的设计模式
- 代理（JDK 生成动态代理对象）
- 责任链（通知的链式调用）
- 单例（比较器 [OrderComparator](src/com/mafei/spring/core/OrderComparator.java)）
- 适配器（适配各种销毁方法的调用）
- 工厂（ObjectFactory）

## 功能测试 Demo
在 `com.mafei.test` 包下