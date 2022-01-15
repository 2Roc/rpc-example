package com.github.rpc.server;

import com.github.rpc.NameService;
import com.github.rpc.RpcAccessPoint;
import com.github.rpc.hello.HelloService;
import com.github.rpc.spi.ServiceSupport;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.File;
import java.net.URI;

@Slf4j
public class Server {
    public static void main(String [] args) throws Exception {

        String serviceName = HelloService.class.getCanonicalName();
        File tmpDirFile = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tmpDirFile, "rpc_example_name_service.data");
        HelloService helloService = new HelloServiceImpl();
        log.info("创建并启动RpcAccessPoint...");
        try(RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);
            Closeable ignored = rpcAccessPoint.startServer()) {
            NameService nameService = rpcAccessPoint.getNameService(file.toURI());
            assert nameService != null;
            log.info("向RpcAccessPoint注册{}服务...", serviceName);
            URI uri = rpcAccessPoint.addServiceProvider(helloService, HelloService.class);
            log.info("服务名: {}, 向NameService注册...", serviceName);
            nameService.registerService(serviceName, uri);
            log.info("开始提供服务，按任何键退出.");
            //noinspection ResultOfMethodCallIgnored
            System.in.read();
            log.info("Bye!");
        }
    }

}
