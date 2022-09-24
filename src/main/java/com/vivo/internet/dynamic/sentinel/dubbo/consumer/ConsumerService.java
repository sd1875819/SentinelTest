package com.vivo.internet.dynamic.sentinel.dubbo.consumer;

import com.vivo.internet.demo.dubbo.provider.facade.HelloService;
import org.springframework.stereotype.Service;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * 此类只是demo示例，上线前请删除
 * 同时删除pom文件中的demo-dubbo依赖
 */
@Deprecated
@Service
public class ConsumerService {
    @DubboReference
    private HelloService helloService;

    public String sayHello(String msg){
        return helloService.sayHello(msg);
    }
}
