# rpc-framework

## 概要信息

基于netty实现的rpc框架，兼容Spring Boot。

## 指南

### 目录

* annotation 注解
* config 配置
* kernel 核心包
    * factory 动态代理Bean工厂
    * netty
        * client 客户端
        * server 服务端
    * processor Bean处理器
    * proxy 动态代理类
    * scanner 扫描工具
* loadbalance 负载均衡

# rpc-framework 快速入门

## 本地开发流程

### 参数配置

```properties
rpc.serverPort=25601
rpc.clientPort=25602
```

> 说明：server端口默认25601，client端口默认25602。

### 编写DTO对象

```java
import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private String name;
    private int age;
}
```

> 说明：DTO对象必须要实现**Serializable**接口。

### 设计服务接口类

```java
public interface SampleService {
    String hello();

    User getUser(String name);
}
```

### 编写服务实现类

```java
import cn.zhangbo.frameworke.rpc.annotation.RpcService;

@RpcService
public class SampleServiceImpl implements SampleService {

    @Override
    public String hell() {
        return "hello world";
    }

    @Override
    public User getUser(String name) {
        return null;
    }
}
```

> 说明：必须使用@RpcService注解，会自动注入到Spring中。

### 编写服务引用逻辑

```java
import cn.zhangbo.frameworke.rpc.annotation.RpcConsumer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @RpcConsumer
    private SampleService sampleService;

    @RequestMapping("/")
    @ResponseBody
    public String sayHello() {
        return sampleService.hello();
    }
}
```

> 说明：引用服务必须使用@RpcConsumer注解。