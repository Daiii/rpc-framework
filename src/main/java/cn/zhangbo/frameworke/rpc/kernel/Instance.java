package cn.zhangbo.frameworke.rpc.kernel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class Instance implements Serializable {

    private String serviceName;

    private String host;

    private Integer port;
}
