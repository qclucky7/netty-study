package com.netty.study.springutils;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author WangChen
 * @since 2021-02-04 14:40
 **/
public class UriComponentsBuilderTest {

    @Test
    public void test0(){


        String s = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/v1/user/{id}/{id}")
                .queryParam("page", 1)
                .queryParam("size", 10)
                .queryParam("search", "")
                .queryParam("sort", "")
                .buildAndExpand("123","456")
                .toUriString();

        System.out.println(s);


        List<User> users = Arrays.asList(
                new User("001", Arrays.asList(
                        new User("123"),
                        new User("456")
                )),
                new User("002", Arrays.asList(
                        new User("789", Arrays.asList(
                                new User("100"),
                                new User("200")
                        )),
                        new User("987")
                ))
        );

        Map<String, List<List<User>>> collect = users.stream()
                .flatMap(user -> user.getUsers().stream())
                .collect(Collectors.groupingBy(user ->
                                Optional.ofNullable(user.name).orElse(""),
                        Collectors.mapping(User::getUsers, Collectors.toList())));

        Map<String, List<List<User>>> collect2 = users.stream()
                .flatMap(user -> user.getUsers().stream())
                .collect(Collectors.groupingBy(user ->
                                user.name == null ? "" : user.name,
                        Collectors.mapping(User::getUsers, Collectors.toList())));

        Map<String, List<List<User>>> collect3 = users.stream()
                .flatMap(user -> user.getUsers().stream())
                .collect(Collectors.groupingBy(user -> {
                            if(StringUtils.isBlank(user.getName())){
                                return "";
                            } else {
                                return user.getName();
                            }
                }, Collectors.mapping(User::getUsers, Collectors.toList())));


        System.out.println(collect.toString());


    }



    static class User{

        private String name;

        private List<User> users;

        public User(String name, List<User> users) {
            this.name = name;
            this.users = users;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User(String name) {
            this.name = name;
        }

        public User(List<User> users) {
            this.users = users;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", users=" + users +
                    '}';
        }
    }
}

