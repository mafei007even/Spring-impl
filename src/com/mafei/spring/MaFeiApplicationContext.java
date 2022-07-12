package com.mafei.spring;

import com.mafei.spring.anno.Autowired;
import com.mafei.spring.anno.Component;
import com.mafei.spring.anno.ComponentScan;
import com.mafei.spring.anno.Scope;
import com.mafei.spring.interfaces.ApplicationContextAware;
import com.mafei.spring.interfaces.BeanNameAware;
import com.mafei.spring.interfaces.BeanPostProcessor;
import com.mafei.spring.interfaces.InitializingBean;
import com.mafei.spring.aop.AnnotationAwareAspectJAutoProxyCreator;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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

    private Class configClass;

    /**
     * beanName -> BeanDefinition
     */
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 单例池： beanName -> beanObj
     */
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

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

                                // 判断是不是 BeanPostProcessor
                                // 这里不能用 instanceOf，因为 instanceof 针对的是实例对象是不是某个类型，而现在只有 class，没有实例对象
                                if (BeanPostProcessor.class.isAssignableFrom(cls)) {
                                    BeanPostProcessor bean = (BeanPostProcessor) cls.newInstance();
                                    beanPostProcessorList.add(bean);
                                }

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
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<");

        registerCommonBeanPostProcessor();

        preInstantiateSingletons();
    }

    private void registerCommonBeanPostProcessor() {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setType(AnnotationAwareAspectJAutoProxyCreator.class);
        beanDefinition.setScope("singleton");
        // beanDefinitionMap.put("internalAutoProxyCreator", beanDefinition);
        Object internalAutoProxyCreator = createBean("internalAutoProxyCreator", beanDefinition);
        beanPostProcessorList.add((BeanPostProcessor) internalAutoProxyCreator);
    }

    private void preInstantiateSingletons() {
        // 将扫描到的单例 bean 创建出来放到单例池中
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        });
    }

    /**
     * 在创建 bean 时完成「依赖注入」
     * <p>
     * createBean 方法就是在模拟 bean 的声明周期：
     * 就是指 Spring 中的一个Bean 是怎么创建出来的，有哪些步骤
     * 步骤 1：实例化 bean
     * Object instance = cls.getConstructor().newInstance();
     * <p>
     * 步骤 2：给bean进行依赖注入
     * 先 byType 再 byName
     * <p>
     * 步骤 3：BeanNameAware 回调
     * <p>
     * 步骤 4：InitializingBean 初始化
     * <p>
     * 步骤 5：BeanPostProcessor（AOP）
     *
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        try {
            // 创建对象
            Object instance = clazz.getConstructor().newInstance();
            // 依赖注入阶段，执行 bean 后处理器的 postProcessProperties 方法
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    // 去Spring 容器中找名为 field.getName() 的 bean，赋值给 instance
                    field.set(instance, getBean(field.getName()));
                }
            }

            // 各种 Aware 回调
            // 回调是：告诉某个东西给对象
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) (instance)).setBeanName(beanName);
            }
            if (instance instanceof ApplicationContextAware) {
                ((ApplicationContextAware) (instance)).setApplicationContext(this);
            }


            // @PostConstruct 初始化前
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }

            // TODO  解析 @PostConstruct 执行初始化方法

            // InitializingBean 初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) (instance)).afterPropertiesSet();
            }

            // @PostConstruct 初始化后
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            }

            // 如果有 aop 的话，这里的 instance 返回的是 aop 后的一个代理对象
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
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
            String scope = beanDefinition.getScope();
            // 单例
            if ("singleton".equals(scope)) {
                // 从单例池中 get
                Object bean = singletonObjects.get(beanName);
                // 有这个 beanDefinition，但这个 bean 还没创建出来且放入单例池中，就会 bean == null
                // 在 createBean 时进行依赖注入的时候，可能某个依赖类还没创建，在这里进行创建
                if (bean == null) {
                    bean = createBean(beanName, beanDefinition);
                    singletonObjects.put(beanName, bean);
                }
                return bean;
            } else { // 多例
                return createBean(beanName, beanDefinition);
            }
        }
    }

    public List<Class<?>> getAllBeanClass() {
        return beanDefinitionMap.values().stream().map((Function<BeanDefinition, Class<?>>) BeanDefinition::getType).toList();
    }

    public ArrayList<String> getBeanNames () {
        Enumeration<String> keys = beanDefinitionMap.keys();
        ArrayList<String> ret = new ArrayList<>();
        while (keys.hasMoreElements()) {
            String beanName = keys.nextElement();
            ret.add(beanName);
        }
        return ret;
    }

}
