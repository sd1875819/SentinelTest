package com.vivo.internet.dynamic.sentinel.controller;

import com.vivo.internet.dynamic.sentinel.dynamic.sentinel.TestSentinelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 此类只是demo示例，上线前请删除
 */
@Deprecated
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private TestSentinelService testSentinelService;

    //启动spring工程后，通过在浏览器访问localhost:8080/demo/test 在浏览器输出ok，来确认该工程是否启动成功
    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    //启动spring工程后，通过在浏览器访问localhost:8080/demo/test2 可调用该test2()方法，从而触发里面的testSentinelService.testMehod();逻辑
    @GetMapping("/test2")
    public String test2() {
        testSentinelService.testMehod();
        return "ok";
    }
}
