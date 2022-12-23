package cn.zhangbo.frameworke.rpc.kernel.netty.client;

import cn.zhangbo.frameworke.rpc.kernel.Invocation;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;

    private Invocation invocation;

    private Object result;

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        result = msg;
        notify();
    }

    @Override
    public synchronized Object call()
            throws Exception {
        context.writeAndFlush(this.invocation);
        wait();
        return result;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }
}
