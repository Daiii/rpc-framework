package cn.zhangbo.frameworke.rpc.kernel.factory;

import cn.zhangbo.frameworke.rpc.kernel.proxy.ServiceProxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

public class ProxyFactoryBean<T> implements FactoryBean<T> {

    @Autowired
    private ServiceProxy proxy;

    private Class<?> interfaceClass;

    public ProxyFactoryBean(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, proxy);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
