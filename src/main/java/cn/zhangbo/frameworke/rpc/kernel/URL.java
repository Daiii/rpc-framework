package cn.zhangbo.frameworke.rpc.kernel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class URL implements Serializable {

    private String hostname;

    private int port;
}
