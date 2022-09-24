package com.vivo.internet.dynamic.sentinel.dubbo.consumer;

import org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration;
import org.apache.dubbo.spring.boot.autoconfigure.DubboRelaxedBindingAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 此类只是demo示例，上线前请删除
 * 测试用例说明：
 * 1，测试服务消费者，基本逻辑是首先启动服务提供者，本测试用例注入spring ，来调用服务提供者，达到测试的目的
 * 2，vivo.boot.config.disable=false用来关闭vivo-boot-config（配置中心）的功能
 * 3，@SpringBootTest注解中配置的k-v，会覆盖application.properties中的配置，也就是说本测试用例会读取application.properties中的配置
 *
 */
@Deprecated
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsumerServiceTest.DemoConsumerServiceImplTestConfiguration.class,
        properties = { "vivo.boot.config.disable=true", "dubbo.application.name=demo-dubbo",
		"dubbo.registry.address=zookeeper://zookeeper.test.vmic.xyz:2183", "dubbo.consumer.check=false"}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ConsumerServiceTest {
	@Autowired
	private ConsumerService consumerService;

	@Test
	public void sayHelloTest() {
		System.out.println(consumerService.sayHello("hello"));
	}

    @Configuration
	@ComponentScan("com.vivo.internet.dynamic.sentinel.dubbo.consumer")
	@Import({ DubboRelaxedBindingAutoConfiguration.class, DubboAutoConfiguration.class })
	public static class DemoConsumerServiceImplTestConfiguration {

	}
}
