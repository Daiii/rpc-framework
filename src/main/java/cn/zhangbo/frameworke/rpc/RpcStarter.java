package cn.zhangbo.frameworke.rpc;

import cn.zhangbo.frameworke.rpc.config.RpcConfig;
import cn.zhangbo.frameworke.rpc.kernel.netty.client.NettyClient;
import cn.zhangbo.frameworke.rpc.kernel.netty.server.NettyServer;
import cn.zhangbo.frameworke.rpc.kernel.proxy.ServiceProxy;
import cn.zhangbo.frameworke.rpc.registry.RegistryFactory;
import cn.zhangbo.frameworke.rpc.registry.RegistryService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({RpcConfig.class})
public class RpcStarter {

    @Bean
    public ServiceProxy serviceProxy() {
        return new ServiceProxy();
    }

    @Bean
    public NettyServer nettyServer(RpcConfig config) {

        return new NettyServer("127.0.0.1", config.getServerPort());
    }

    @Bean
    public NettyClient nettyClient(RpcConfig config) {
        return new NettyClient("127.0.0.1", config.getClientPort());
    }

    @Bean
    public RegistryService registryService(RpcConfig config) {
        return RegistryFactory.getRegistry(config.getRegisterProtocol());
    }

    @Bean
    public ComponentRegistrar componentRegistrar() {
        return new ComponentRegistrar();
    }
}
