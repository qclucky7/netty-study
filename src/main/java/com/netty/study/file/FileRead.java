package com.netty.study.file;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WangChen
 * @since 2020-12-07 10:44
 **/
public class FileRead {

    @Test
    public void myTest() throws IOException {

        List<String> collect = Files.lines(Paths.get("D:\\netty-study\\src\\main\\resources\\file\\protocol"), StandardCharsets.UTF_8)
                .collect(Collectors.toList());


        System.out.println(collect.toString());


    }


    @Test
    public void myTest1() throws IOException {

        byte[] bytes = Files.readAllBytes(Paths.get("D:\\netty-study\\src\\main\\resources\\file\\config.json"));

        String s = new String(bytes, StandardCharsets.UTF_8);

        System.out.println(s);

    }


}
