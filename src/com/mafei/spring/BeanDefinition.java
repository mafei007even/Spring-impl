package com.mafei.spring;

/**
 * @author mafei007
 * @date 2022/6/29 20:08
 */
public class BeanDefinition {

    private Class type;
    private String scope;

    public boolean isSingleton() {
        return "singleton".equals(scope);
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "type=" + type +
                ", scope='" + scope + '\'' +
                '}';
    }
}
