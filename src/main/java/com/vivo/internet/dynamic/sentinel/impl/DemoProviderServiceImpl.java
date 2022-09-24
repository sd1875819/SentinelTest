package com.vivo.internet.dynamic.sentinel.impl;

import com.vivo.internet.dynamic.sentinel.DemoProviderService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * 此类只是demo示例，上线前请删除
 * 同时删除pom文件中的demo-dubbo依赖
 */
@DubboService
@Deprecated
public class DemoProviderServiceImpl implements DemoProviderService {

    @Override
    public String sayHello(String name) {
        System.out.println(name);
        return "Hello " + name;
    }

}
