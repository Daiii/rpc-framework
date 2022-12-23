package cn.zhangbo.frameworke.rpc.registry;

import cn.hutool.core.util.StrUtil;
import cn.zhangbo.frameworke.rpc.common.RegistrarEnum;

public class RegistryFactory {

    public static RegistryService getRegistry(String protocol) {
        if (StrUtil.equals(protocol, RegistrarEnum.redis.toString())) {
            return new RedisRegistry();
        }
        return null;
    }
}
