package cn.zhangbo.frameworke.rpc.registry;

import cn.zhangbo.frameworke.rpc.kernel.URL;

public interface RegistryService {

    void register(URL url);

    void unregister(URL url);
}
