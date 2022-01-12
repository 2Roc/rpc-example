## 环境要求

运行示例之前需要先安装：

* JDK 1.8
* Maven 3.8.0

## 下载编译源代码

```bash
$git clone git@github.com:rpc/rpc-example.git
$cd rpc-example
$mvn package
```

## 启动服务端

```bash
$java -jar server/target/server-1.0-SNAPSHOT-jar-with-dependencies.jar
[main] INFO com.github.rpc.server.Server - 创建并启动RpcAccessPoint...
[main] INFO com.github.rpc.transport.RequestHandlerRegistry - Load request handler, type: 0, class: com.github.rpc.transport.RpcRequestHandler.
[main] INFO com.github.rpc.server.Server - 向RpcAccessPoint注册com.github.rpc.hello.HelloService服务...
[main] INFO com.github.rpc.server.Server - 服务名: com.github.rpc.hello.HelloService, 向NameService注册...
[main] INFO com.github.rpc.nameservice.LocalFileNameService - Register service: com.github.rpc.hello.HelloService, uri: rpc://localhost:9999.
[main] INFO com.github.rpc.serialize.SerializeSupport - Found serializer, class: com.github.rpc.nameservice.Metadata, type: 100.
[main] INFO com.github.rpc.serialize.SerializeSupport - Found serializer, class: java.lang.String, type: 0.
[main] INFO com.github.rpc.serialize.SerializeSupport - Found serializer, class: com.github.rpc.client.stubs.RpcRequest, type: 101.
[main] INFO com.github.rpc.nameservice.LocalFileNameService - Metadata:
	Classname: com.github.rpc.hello.HelloService
	URIs:
		rpc://localhost:9999

[main] INFO com.github.rpc.server.Server - 开始提供服务,按任何键退出.
```

## 运行客户端

```bash
java -jar client/target/client-1.0-SNAPSHOT-jar-with-dependencies.jar
[main] INFO com.github.rpc.serialize.SerializeSupport - Found serializer, class: com.github.rpc.nameservice.Metadata, type: 100.
[main] INFO com.github.rpc.serialize.SerializeSupport - Found serializer, class: java.lang.String, type: 0.
[main] INFO com.github.rpc.serialize.SerializeSupport - Found serializer, class: com.github.rpc.client.stubs.RpcRequest, type: 101.
[main] INFO com.github.rpc.nameservice.LocalFileNameService - Metadata:
	Classname: com.github.rpc.hello.HelloService
	URIs:
		rpc://localhost:9999

[main] INFO com.github.rpc.client.Client - 找到服务com.github.rpc.hello.HelloService，提供者: rpc://localhost:9999.
[main] INFO com.github.rpc.client.Client - 请求服务, name: Master MQ...
[main] INFO com.github.rpc.client.Client - 收到响应: Hello, Master MQ.
```

## RPC框架功能定义

RPC框架对外提供的所有服务定义在一个接口RpcAccessPoint中：

```java
/**
 * RPC框架对外提供的服务接口
 */
public interface RpcAccessPoint extends Closeable{
    /**
     * 客户端获取远程服务的引用
     * @param uri 远程服务地址
     * @param serviceClass 服务的接口类的Class
     * @param <T> 服务接口的类型
     * @return 远程服务引用
     */
    <T> T getRemoteService(URI uri, Class<T> serviceClass);

    /**
     * 服务端注册服务的实现实例
     * @param service 实现实例
     * @param serviceClass 服务的接口类的Class
     * @param <T> 服务接口的类型
     * @return 服务地址
     */
    <T> URI addServiceProvider(T service, Class<T> serviceClass);

    /**
     * 服务端启动RPC框架，监听接口，开始提供远程服务。
     * @return 服务实例，用于程序停止的时候安全关闭服务。
     */
    Closeable startServer() throws Exception;
}
```

注册中心的接口NameService：

```java
/**
 * 注册中心
 */
public interface NameService {
    /**
     * 注册服务
     * @param serviceName 服务名称
     * @param uri 服务地址
     */
    void registerService(String serviceName, URI uri) throws IOException;

    /**
     * 查询服务地址
     * @param serviceName 服务名称
     * @return 服务地址
     */
    URI lookupService(String serviceName) throws IOException;
}

```

## 例子

需要先定义一个服务接口：

```java
public interface HelloService {
    String hello(String name);
}
```

客户端：

```java
URI uri = nameService.lookupService(serviceName);
HelloService helloService = rpcAccessPoint.getRemoteService(uri, HelloService.class);
String response = helloService.hello(name);
logger.info("收到响应: {}.", response);
```

服务端：

定义一个HelloService的实现：

```java
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        String ret = "Hello, " + name;
        return ret;
    }
}
```

然后，把实现注册到RPC框架上，并启动RPC服务：

```java
rpcAccessPoint.startServer();
URI uri = rpcAccessPoint.addServiceProvider(helloService, HelloService.class);
nameService.registerService(serviceName, uri);
```

## 项目结构

Module | 说明
-- | --
client | 例子：客户端
server | 例子：服务端
rpc-api | RPC框架接口
hello-service-api | 例子：接口定义
rpc-netty | 基于Netty实现的RPC框架