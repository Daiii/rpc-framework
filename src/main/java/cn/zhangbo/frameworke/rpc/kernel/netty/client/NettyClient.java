package cn.zhangbo.frameworke.rpc.kernel.netty.client;

import cn.zhangbo.frameworke.rpc.kernel.Invocation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import jakarta.annotation.PostConstruct;

import java.util.Map;
import java.util.concurrent.*;

public class NettyClient {

    public String remoteIp;

    public int port;

    private Bootstrap bootstrap;

    public NettyClient(String remoteIp, int port) {
        this.remoteIp = remoteIp;
        this.port = port;
    }

    public NettyClientHandler client = null;

    private final Map<String, SynchronousQueue<String>> results = new ConcurrentHashMap<>();

    private static ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    @PostConstruct
    public void init() {
        client = new NettyClientHandler();
        bootstrap = new Bootstrap().remoteAddress(remoteIp, port);
        NioEventLoopGroup worker = new NioEventLoopGroup(1);
        bootstrap.group(worker).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                pipeline.addLast("encoder", new ObjectEncoder());
                pipeline.addLast("handler", client);
            }
        });


    }

    public Object send(String hostName, Integer port, Invocation invocation) {
        Channel channel = null;
        Object result = null;
        try {
            client.setInvocation(invocation);
            channel = bootstrap.connect().sync().channel();
            result = EXECUTOR.submit(client).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            if (channel != null && channel.isActive()) {
                channel.close();
            }
        }
        return result;
    }

}
