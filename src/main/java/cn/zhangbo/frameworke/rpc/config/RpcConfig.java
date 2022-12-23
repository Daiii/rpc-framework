package cn.zhangbo.frameworke.rpc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rpc", ignoreInvalidFields = true)
public class RpcConfig {

    private int serverPort = 25601;

    private int clientPort = 25602;

    private String registerProtocol = "redis";
}
