package cn.zhangbo.frameworke.rpc.kernel.netty;

import cn.zhangbo.frameworke.rpc.kernel.Invocation;
import cn.zhangbo.frameworke.rpc.kernel.Task;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DispatcherHandler implements ChannelHandler {

    private ChannelHandler channelHandler;

    private ExecutorService executorService;

    public DispatcherHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
        executorService = Executors.newFixedThreadPool(10);

    }

    @Override
    public void handler(ChannelHandlerContext ctx, Invocation invocation)
            throws Exception {
        executorService.execute(new Task(invocation, channelHandler, ctx));
    }
}
