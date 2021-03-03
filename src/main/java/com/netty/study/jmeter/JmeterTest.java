package com.netty.study.jmeter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WangChen
 * @since 2020-12-18 16:30
 **/
@RestController
public class JmeterTest {

    @GetMapping("/test")
    public void test(){
        System.out.println("execute!");
    }
}
