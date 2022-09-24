package com.vivo.internet.dynamic.sentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 *  请注意，启动类应当放在于最顶层包目录！
 */
@SpringBootApplication
public class DynamicSentinelApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        setEnvironmentInDev();
        SpringApplication.run(DynamicSentinelApplication.class, args);
    }

    public static void setEnvironmentInDev() {
        if (System.getProperty("app.name") != null)
            return;

        System.setProperty("app.name", "vivo-boot-demo");
        System.setProperty("app.env", "test");
        System.setProperty("app.loc", "sz-sk");
        System.setProperty("config.host", "http://vivocfg-agent.test.vivo.xyz/vivocfg");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DynamicSentinelApplication.class);
    }
}
