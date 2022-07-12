package com.mafei.spring.aop;

import com.mafei.spring.MaFeiApplicationContext;
import com.mafei.spring.aop.advisor.Advice;
import com.mafei.spring.aop.advisor.Advisor;
import com.mafei.spring.aop.advisor.MethodMatcher;
import com.mafei.spring.aop.advisor.Pointcut;
import com.mafei.spring.core.OrderComparator;
import com.mafei.spring.core.Ordered;
import com.mafei.spring.interfaces.ApplicationContextAware;
import com.mafei.spring.interfaces.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * bean 后处理器，对符合条件的 bean 进行 aop 代理增强，创建代理对象
 *
 * @author mafei007
 * @date 2022/7/7 20:23
 */
public class AnnotationAwareAspectJAutoProxyCreator implements BeanPostProcessor, ApplicationContextAware {

    private MaFeiApplicationContext applicationContext;

    private final AspectJAdvisorFactory advisorFactory = new DefaultAspectJAdvisorFactory();

    private List<Advisor> cachedAdvisors;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean != null) {
            return wrapIfNecessary(bean, beanName);
        }
        return bean;
    }


    private Object wrapIfNecessary(Object bean, String beanName) {
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }
        List<Advisor> advisorList = findEligibleAdvisors(bean.getClass(), beanName);
        if (!advisorList.isEmpty()) {
            System.out.println("………………………………………………auto-proxy user class [" + bean.getClass().getName() + "],  beanName[" + beanName + "]");
            Object proxy = createProxy(bean, beanName, advisorList);
            return proxy;
        }
        System.out.println("………………………………………………Did not to auto-proxy user class [" + bean.getClass().getName() + "],  beanName[" + beanName + "]");
        return bean;
    }

    protected boolean isInfrastructureClass(Class<?> beanClass) {
        boolean retVal = Advice.class.isAssignableFrom(beanClass) ||
                Pointcut.class.isAssignableFrom(beanClass) ||
                Advisor.class.isAssignableFrom(beanClass) ||
                this.advisorFactory.isAspect(beanClass);
        if (retVal) {
            // logger.trace("Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
            System.out.println("………………………………………………Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
        }
        return retVal;
    }

    private Object createProxy(Object bean, String beanName, List<Advisor> advisorList) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(bean);
        proxyFactory.addAdvisors(advisorList);
        System.out.println("给 " + beanName + " 创建代理，有 " + advisorList.size() + " 个切面。");
        return proxyFactory.getProxy();
    }

    private List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
        // 如果最终的 Advisor 列表不为空，再在开头位置添加一个 ExposeInvocationInterceptor
        // extendAdvisors(eligibleAdvisors);
        if (!eligibleAdvisors.isEmpty()) {
            OrderComparator.sort(eligibleAdvisors);
        }
        return eligibleAdvisors;
    }

    private List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
        if (candidateAdvisors.isEmpty()) {
            return candidateAdvisors;
        }
        List<Advisor> eligibleAdvisors = new ArrayList<>(candidateAdvisors.size());
        Method[] methods = beanClass.getDeclaredMethods();

        // 遍历 bean 目标类型的所有方法，包括继承来的接口方法等
        // 继承的方法没写

        // 双重 for 循环
        for (Advisor advisor : candidateAdvisors) {
            MethodMatcher methodMatcher = advisor.getPointcut().getMethodMatcher();
            for (Method method : methods) {
                if (methodMatcher.matches(method, beanClass)) {
                    eligibleAdvisors.add(advisor);
                    break;
                }
            }
        }
        return eligibleAdvisors;
    }

    private List<Advisor> findCandidateAdvisors() {
        List<Advisor> advisors = findCandidateAdvisorsInBeanFactory();
        advisors.addAll(findCandidateAdvisorsInAspect());
        return advisors;
    }

    /**
     * 遍历 beanFactory 中所有 bean，找到被 @Aspect 注解标注的 bean，再去 @Aspect 类中封装 Advisor
     *
     * @return
     */
    private List<Advisor> findCandidateAdvisorsInAspect() {
        if (this.cachedAdvisors != null) {
            return this.cachedAdvisors;
        }
        List<Class<?>> allClass = applicationContext.getAllBeanClass();
        List<Advisor> advisors = new ArrayList<>();

        for (Class<?> cls : allClass) {
            if (this.advisorFactory.isAspect(cls)) {
                List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(cls);
                advisors.addAll(classAdvisors);
            }
        }
        this.cachedAdvisors = advisors;
        return this.cachedAdvisors;
    }

    /**
     * 去容器中拿所有低级 Advisor
     *
     * @return
     */
    private List<Advisor> findCandidateAdvisorsInBeanFactory() {
        return new ArrayList<>();
    }

    @Override
    public void setApplicationContext(MaFeiApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
