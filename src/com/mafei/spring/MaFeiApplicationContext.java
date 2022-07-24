package com.mafei.spring;

import com.mafei.spring.anno.*;
import com.mafei.spring.aop.AnnotationAwareAspectJAutoProxyCreator;
import com.mafei.spring.aop.proxy.LazyInjectTargetSource;
import com.mafei.spring.aop.proxy.ProxyFactory;
import com.mafei.spring.interfaces.*;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 容器启动
 * BeanDefinition 扫描
 * Bean的生命周期
 * 单例与多例Bean
 * 依赖注入
 * AOP
 * Aware回调
 * 初始化
 * BeanPostProcessor
 *
 * @author mafei007
 * @date 2022/6/29 19:28
 */
public class MaFeiApplicationContext {

    private final Class configClass;

    /**
     * beanName -> BeanDefinition
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * Cache of singleton factories: bean name to ObjectFactory.
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

    /**
     * Cache of early singleton objects: bean name to bean instance.
     */
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

    /**
     * 单例池： beanName -> beanObj
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * Names of beans that are currently in creation.
     */
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    /**
     * Names of Prototype beans that are currently in creation.
     */
    private final ThreadLocal<Object> prototypesCurrentlyInCreation = new ThreadLocal<>();

    private final List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public MaFeiApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 扫描 -> 得到一系列 BeanDefinition，放入 beanDefinitionMap
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            // 扫描路径 com.mafei.test
            String path = componentScanAnnotation.value();
            // 扫描路径 com/mafei/test
            path = path.replace(".", "/");

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            // 取得绝对路径: /Users/mafei007/AppProjects/IdeaProjects/spring_study/out/production/simple_impl/com/mafei/test
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());

            // 遍历目录下的所有文件，都是 componentScan 需要扫描的，这里只遍历了一层目录
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    String fileName = f.getAbsolutePath();
                    System.out.println(fileName);

                    if (fileName.endsWith(".class")) {
                        // 提取出 class 对象，需要类的全限定名
                        // com/mafei/test/Usertest
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                        // com.mafei.test.Usertest
                        className = className.replace("/", ".");
                        System.out.println(className);
                        try {
                            Class<?> cls = classLoader.loadClass(className);
                            // 当前 class 是个 Bean 对象
                            if (cls.isAnnotationPresent(Component.class)) {
                                Component componentAnno = cls.getAnnotation(Component.class);
                                String beanName = componentAnno.value();

                                // 生成默认的 beanName
                                if ("".equals(beanName)) {
                                    beanName = Introspector.decapitalize(cls.getSimpleName());
                                }

                                // 生成 BeanDefinition，解析 单例bean or 多例bean
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(cls);
                                if (cls.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = cls.getAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                } else {
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<");

        registerBeanPostProcessors();

        preInstantiateSingletons();
    }

    /**
     * 创建所有 Bean 后处理器，放入 singletonObjects 容器中，并注册到 beanPostProcessorList
     * <p>
     * 在后续的 preInstantiateSingletons() 初始化单例中，会先从容器中获取，获取不到再创建
     * Bean 后处理器属于单例，提前创建好了并放入容器，所以 Bean 后处理器并不会重复创建
     */
    private void registerBeanPostProcessors() {
        registerCommonBeanPostProcessor();
        /*
          1. 从 beanDefinitionMap 中找出所有的 BeanPostProcessor
          2. 创建 BeanPostProcessor 放入容器
          3. 将创建的 BeanPostProcessor 注册到 beanPostProcessorList

          这里的写法：先注册的 BeanPostProcessor 会对后创建的 BeanPostProcessor 进行拦截处理，
          BeanPostProcessor 的创建走 bean 的生命周期流程
         */
        this.beanDefinitionMap.entrySet()
                .stream()
                .filter((entry) -> BeanPostProcessor.class.isAssignableFrom(entry.getValue().getType()))
                .forEach((entry) -> {
                    BeanPostProcessor beanPostProcessor = (BeanPostProcessor) getBean(entry.getKey());
                    this.beanPostProcessorList.add(beanPostProcessor);
                });
    }

    /**
     * 注册常用的 Bean 后处理器到 beanDefinitionMap 中
     */
    private void registerCommonBeanPostProcessor() {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setType(AnnotationAwareAspectJAutoProxyCreator.class);
        beanDefinition.setScope("singleton");
        beanDefinitionMap.put("internalAutoProxyCreator", beanDefinition);
    }

    private void preInstantiateSingletons() {
        // 将扫描到的单例 bean 创建出来放到单例池中
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            if (beanDefinition.isSingleton()) {
                getBean(beanName);
            }
        });
    }

    /**
     * 创建 bean
     * createBean 方法就是在模拟 bean 的声明周期
     * 创建、依赖注入、初始化
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        beforeCreation(beanName, beanDefinition);
        try {
            // 创建对象
            Object bean = createBeanInstance(beanName, beanDefinition);

            // 如果当前创建的是单例对象，依赖注入前将工厂对象 fa 存入三级缓存 singletonFactories 中
            if (beanDefinition.isSingleton()) {
                System.out.println("🐶🐶🐶🐶 createBean：Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references");
                this.singletonFactories.put(beanName, new ObjectFactory<Object>() {
                    @Override
                    public Object getObject() throws RuntimeException {
                        Object exposedObject = bean;
                        for (BeanPostProcessor beanPostProcessor : MaFeiApplicationContext.this.beanPostProcessorList) {
                            if (beanPostProcessor instanceof SmartInstantiationAwareBeanPostProcessor) {
                                exposedObject = ((SmartInstantiationAwareBeanPostProcessor) beanPostProcessor).getEarlyBeanReference(exposedObject, beanName);
                            }
                        }
                        return exposedObject;
                    }
                });
                this.earlySingletonObjects.remove(beanName);
            }

            Object exposedObject = bean;
            populateBean(beanName, beanDefinition, bean);
            exposedObject = initializeBean(beanName, beanDefinition, exposedObject);

            // 去二级缓存 earlySingletonObjects 中查看有没有当前 bean，
            // 如果有，说明发生了循环依赖，返回缓存中的 a 对象（可能是代理对象也可能是原始对象，主要看有没有切点匹配到 bean）。
            if (beanDefinition.isSingleton()) {
                Object earlySingletonReference = getSingleton(beanName, false);
                if (earlySingletonReference != null) {
                    exposedObject = earlySingletonReference;
                }
            }

            return exposedObject;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            afterCreation(beanName, beanDefinition);
        }
    }

    private void afterCreation(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            afterSingletonCreation(beanName);
        } else {
            afterPrototypeCreation(beanName);
        }
    }

    @SuppressWarnings("unchecked")
    private void afterPrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        } else if (curVal instanceof Set) {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }

    private void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.contains(beanName)) {
            // 可能被别的线程修改了
            throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
        }
        this.singletonsCurrentlyInCreation.remove(beanName);
    }

    private void beforeCreation(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            beforeSingletonCreation(beanName);
        } else {
            beforePrototypeCreation(beanName);
        }
    }

    @SuppressWarnings("unchecked")
    private void beforePrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal != null &&
                (curVal.equals(beanName) || (curVal instanceof Set && ((Set<?>) curVal).contains(beanName)))) {
            throw new IllegalStateException("Error creating prototype bean with name '" + beanName + "': "
                    + "Requested bean is currently in creation: Is there an unresolvable circular reference?");
        }
        // 加入 ThreadLocal
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
        } else if (curVal instanceof String) {
            Set<String> beanNameSet = new HashSet<>();
            beanNameSet.add((String) curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        } else {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.add(beanName);
        }
    }

    private void beforeSingletonCreation(String beanName) {
        if (this.singletonsCurrentlyInCreation.contains(beanName)) {
            throw new IllegalStateException("Error creating singleton bean with name '" + beanName + "': "
                    + "Requested bean is currently in creation: Is there an unresolvable circular reference?");
        }
        this.singletonsCurrentlyInCreation.add(beanName);
    }

    /**
     * 初始化阶段，包含：Aware回调、初始化前、初始化、初始化后
     *
     * @param beanName
     * @param beanDefinition
     * @param bean
     * @return
     */
    private Object initializeBean(String beanName, BeanDefinition beanDefinition, Object bean) {
        // 0️⃣ 各种 Aware 回调
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) (bean)).setBeanName(beanName);
        }
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) (bean)).setApplicationContext(this);
        }

        // 1️⃣ 初始化前
        // TODO  BeanPostProcessor 解析 @PostConstruct 执行初始化方法
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            bean = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
        }

        // 2️⃣ 初始化
        if (bean instanceof InitializingBean) {
            ((InitializingBean) (bean)).afterPropertiesSet();
        }
        // TODO 执行 @Bean(initMethod = “myInit”) 指定的初始化方法（将初始化方法记录在 BeanDefinition 中）

        // 3️⃣ 初始化后，由 AnnotationAwareAspectJAutoProxyCreator 创建 aop 代理
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
        }
        // 如果有 aop 的话，这里的 bean 返回的是 aop 后的一个代理对象
        return bean;
    }

    /**
     * 依赖注入阶段，执行 bean 后处理器的 postProcessProperties 方法
     *
     * @param beanName
     * @param beanDefinition
     * @param bean
     */
    private void populateBean(String beanName, BeanDefinition beanDefinition, Object bean) throws IllegalAccessException, InvocationTargetException {
        Class clazz = beanDefinition.getType();
        // 解析方法上的 Autowired
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Autowired.class)) {
                // 编译时加上 -parameters 参数才能反射获取到参数名
                // 或者编译时加上 -g 参数，使用 ASM 获取到参数名
                String paramName = method.getParameters()[0].getName();
                method.invoke(bean, getBean(paramName));
            }
        }
        // 解析字段上的 Autowired
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                // 去Spring 容器中找名为 field.getName() 的 bean，赋值给 bean
                field.set(bean, getBean(field.getName()));
            }
        }

    }

    /*
    先将 beanDefinition 扫描出来再创建实例，而不是边扫描边创建
    是因为在 createBean 时，要进行依赖注入，需要看看有没有提供某个类的依赖
    所以要先扫描后创建
     */

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            // 单例
            if (beanDefinition.isSingleton()) {
                Object singletonObject = getSingleton(beanName, true);
                // 三处缓存都没有某个 bean，只能 create 了
                if (singletonObject == null) {
                    singletonObject = createBean(beanName, beanDefinition);
                    this.singletonObjects.put(beanName, singletonObject);
                    this.earlySingletonObjects.remove(beanName);
                    this.singletonFactories.remove(beanName);
                }
                return singletonObject;
            } else { // 多例
                return createBean(beanName, beanDefinition);
            }
        }
    }

    public <T> T getBean(String beanName, Class<T> requiredType) {
        return (T) getBean(beanName);
    }

    /**
     * 尝试依次从 3 处缓存中取
     *
     * @param beanName
     * @param allowEarlyReference 是否应该创建早期引用。
     *                            bean 初始化后应该检查二级缓存是否提前创建了 bean，此时 allowEarlyReference 为 false，只检查到二级缓存即可
     * @return
     */
    private Object getSingleton(String beanName, boolean allowEarlyReference) {
        // 一级缓存： 单例池
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            // 二级缓存：提前创建的单例对象池
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                // 三级缓存：单例工厂池
                ObjectFactory<?> objectFactory = this.singletonFactories.get(beanName);
                if (objectFactory != null) {
                    singletonObject = objectFactory.getObject();
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    public List<Class<?>> getAllBeanClass() {
        return beanDefinitionMap.values().stream().map((Function<BeanDefinition, Class<?>>) BeanDefinition::getType).toList();
    }

    public ArrayList<String> getBeanNames() {
        return new ArrayList<>(beanDefinitionMap.keySet());
        /*Enumeration<String> keys = beanDefinitionMap.keys();
        ArrayList<String> ret = new ArrayList<>();
        while (keys.hasMoreElements()) {
            String beanName = keys.nextElement();
            ret.add(beanName);
        }
        return ret;*/
    }

    /**
     * 创建 bean
     * 编译时加上 -parameters 参数才能反射获取到参数名
     * 或者编译时加上 -g 参数，使用 ASM 获取到参数名
     *
     * @param beanName
     * @param beanDefinition
     * @return
     * @throws Throwable
     */
    private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) throws Throwable {
        Class<?> clazz = beanDefinition.getType();
        // 优先使用无参构造
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return constructor.newInstance();
            }
        }
        // 没有无参构造，使用有参构造，随机选一个构造器
        Constructor<?> constructor = constructors[0];
        Object[] args = new Object[constructor.getParameterCount()];
        Parameter[] parameters = constructor.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = null;
            if (parameter.getType().equals(ObjectFactory.class)) { // ObjectFactory 参数
                arg = buildLazyObjectFactory(parameter.getName());
            } else if (parameter.isAnnotationPresent(Lazy.class)) { // 参数加了 @Lazy，生成代理
                arg = buildLazyResolutionProxy(parameter.getName(), parameter.getType());
            } else { // 不是 ObjectFactory 也没加 @Lazy 的，直接从容器中拿
                arg = getBean(parameter.getName());
            }
            args[i] = arg;
        }
        return constructor.newInstance(args);
    }

    private Object buildLazyObjectFactory(String requestingBeanName) {
        return new ObjectFactory<Object>() {
            @Override
            public Object getObject() throws RuntimeException {
                return getBean(requestingBeanName);
            }
        };
    }

    private Object buildLazyResolutionProxy(String requestingBeanName, Class<?> clazz) {
        LazyInjectTargetSource targetSource = new LazyInjectTargetSource(this, requestingBeanName);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(targetSource);
        proxyFactory.setInterfaces(clazz.getInterfaces());
        // 临时的解决方案，JDK 动态代理只能基于接口，要代理的 class 可能本身是个接口，添加进去
        if (clazz.isInterface()) {
            proxyFactory.addInterface(clazz);
        }
        System.out.println("🐷🐷🐷🐷 使用有参构造，为 " + requestingBeanName + " 参数创建代理对象");
        return proxyFactory.getProxy();
    }

}
