package cn.zhangbo.frameworke.rpc.kernel.netty;

import cn.zhangbo.frameworke.rpc.kernel.Invocation;
import io.netty.channel.ChannelHandlerContext;

public interface ChannelHandler {
    void handler(ChannelHandlerContext ctx, Invocation invocation)
            throws Exception;

}
