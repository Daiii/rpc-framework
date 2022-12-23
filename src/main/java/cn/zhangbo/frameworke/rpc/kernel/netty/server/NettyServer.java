package cn.zhangbo.frameworke.rpc.kernel.netty.server;

import cn.zhangbo.frameworke.rpc.annotation.RpcService;
import cn.zhangbo.frameworke.rpc.kernel.netty.DispatcherHandler;
import cn.zhangbo.frameworke.rpc.kernel.netty.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyServer implements InitializingBean, ApplicationContextAware {

    private String hostname;

    private int port;

    private Map<String, Object> rpcService = new ConcurrentHashMap<>();

    public NettyServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void start(String hostname, Integer port) {
        new Thread(() -> {
            EventLoopGroup boss = new NioEventLoopGroup(1);
            EventLoopGroup worker = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker).childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 60));
                        pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                        pipeline.addLast("encoder", new ObjectEncoder());
                        pipeline.addLast("handler", new NettyServerHandler(new DispatcherHandler(new RequestHandler(rpcService))));
                    }
                }).channel(NioServerSocketChannel.class);
                ChannelFuture future = bootstrap.bind(hostname, port).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        }).start();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start(hostname, port);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> services = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (Map.Entry<String, Object> entry : services.entrySet()) {
            Object bean = entry.getValue();
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            for (Class<?> inter : interfaces) {
                rpcService.put(inter.getName(), bean);
            }
        }
    }
}
