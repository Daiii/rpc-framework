package cn.zhangbo.frameworke.rpc.kernel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class Invocation implements Serializable {

    private String interfaceName;

    private String methodName;

    private Class<?>[] paramTypes;

    private Object[] params;

    private List<URL> serviceList;
}
