package cn.zhangbo.frameworke.rpc.kernel.proxy;

import cn.zhangbo.frameworke.rpc.kernel.Invocation;
import cn.zhangbo.frameworke.rpc.kernel.netty.client.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {

    @Autowired
    NettyClient client;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Invocation invocation = new Invocation(method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes(), args, null);
        return client.send(client.remoteIp, client.port, invocation);
    }
}
