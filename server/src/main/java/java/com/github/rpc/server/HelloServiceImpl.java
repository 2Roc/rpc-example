package java.com.github.rpc.server;

import com.github.rpc.hello.HelloService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        log.info("HelloServiceImpl收到: {}.", name);
        String ret = "Hello, " + name;
        log.info("HelloServiceImpl返回: {}.", ret);
        return ret;
    }
}
