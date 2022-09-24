package com.vivo.internet.dynamic.sentinel.dubbo.provider;

import org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration;
import org.apache.dubbo.spring.boot.autoconfigure.DubboRelaxedBinding2AutoConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import  org.apache.dubbo.config.annotation.DubboReference;
import com.vivo.internet.dynamic.sentinel.DemoProviderService;
import com.vivo.internet.dynamic.sentinel.dubbo.provider.DemoProviderServiceImplTest.DemoProviderServiceImplTestConfiguration;

/**
 * 此类只是demo示例，上线前请删除
 * 测试用例说明：
 * 1，测试服务提供者，基本逻辑是首先启动服务提供者，本测试用例自身作为服务消费者，来调用服务提供者，达到测试的目的
 * 2，vivo.boot.config.disable=false用来关闭vivo-boot-config（配置中心）的功能
 * 3，@SpringBootTest注解中配置的k-v，会覆盖application.properties中的配置，也就是说本测试用例会读取application.properties中的配置
 * 4，不需要配置扫描任何spring的bean，dubbo注解扫描也设置为当前测试用例所在package，确保@Reference能dubbo被扫描到
 *
 */
@Deprecated
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoProviderServiceImplTestConfiguration.class, properties = { "vivo.boot.config.disable=true",
		"dubbo.application.name=demo-dubbo", "dubbo.registry.address=zookeeper://zookeeper.test.vmic.xyz:2183",
		"dubbo.protocol.name=dubbo", "dubbo.protocol.port=13300",
		"dubbo.scan.base-packages=com.vivo.internet.dynamic.sentinel.dubbo.provider,com.vivo.internet.dynamic.sentinel.impl" }, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DemoProviderServiceImplTest {
	@DubboReference
	private DemoProviderService demoProviderService;

	@BeforeClass
	public static void setEnvironmentInDev() {
	}

	@Test
	public void sayHelloTest() {
		demoProviderService.sayHello("hello");
	}

	@Configuration
	@Import({ DubboRelaxedBinding2AutoConfiguration.class, DubboAutoConfiguration.class })
	public static class DemoProviderServiceImplTestConfiguration {

	}

}
