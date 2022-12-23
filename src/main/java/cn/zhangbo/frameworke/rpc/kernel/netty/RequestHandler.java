package cn.zhangbo.frameworke.rpc.kernel.netty;

import cn.zhangbo.frameworke.rpc.kernel.Invocation;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.Map;

public class RequestHandler implements ChannelHandler {

    public RequestHandler(Map<String, Object> rpcServices) {
        this.rpcServices = rpcServices;
    }

    private Map<String, Object> rpcServices;

    @Override
    public void handler(ChannelHandlerContext ctx, Invocation invocation) throws Exception {
        Object bean = rpcServices.get(invocation.getInterfaceName());
        if (bean == null) {
            throw new RuntimeException("service not found : " + invocation.getInterfaceName());
        }
        Method method = bean.getClass().getMethod(invocation.getMethodName(), invocation.getParamTypes());
        method.setAccessible(true);
        Object result = method.invoke(bean, invocation.getParams());
        System.out.println(result);
        // 返回服务结果
        if (result != null) {
            ctx.writeAndFlush(result);
        }
    }
}
