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

    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    @GetMapping("/test2")
    public String test2() {
        testSentinelService.testMehod();
        return "ok";
    }
}
