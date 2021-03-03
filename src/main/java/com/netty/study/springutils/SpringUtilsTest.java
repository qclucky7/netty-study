package com.netty.study.springutils;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author WangChen
 * @since 2021-02-04 14:58
 **/
public class SpringUtilsTest {

    @Test
    public void myTest() throws IOException {

        //FileSystemResource fileSystemResource = new FileSystemResource("application.yaml");
        ClassPathResource classPathResource = new ClassPathResource("./application.yaml");
        Properties properties = PropertiesLoaderUtils.loadProperties(classPathResource);

        System.out.println(properties);
    }

    @Test
    public void myTest2() throws InterruptedException {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start("123");
        TimeUnit.SECONDS.sleep(2);
        stopWatch.stop();

        stopWatch.start("456");
        TimeUnit.SECONDS.sleep(1);
        stopWatch.stop();

        stopWatch.start("789");
        TimeUnit.SECONDS.sleep(3);
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }


}
