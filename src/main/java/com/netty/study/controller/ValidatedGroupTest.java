package com.netty.study.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

/**
 * @author WangChen
 * @since 2021-02-05 09:35
 **/
@RestController
public class ValidatedGroupTest {

    @PostMapping("/create")
    public void create(@RequestBody @Validated(CreateUser.class) UserDTO userDTO){

    }


    @PostMapping("/update")
    public void update(@RequestBody @Validated(UpdateUser.class) UserDTO userDTO){

    }


    static class UserDTO{

        @NotBlank(message = "userId不能为空", groups = UpdateUser.class)
        private String userId;
        @NotBlank(message = "name不能为空", groups = {CreateUser.class, UpdateUser.class})
        private String name;
        @NotBlank(message = "phone不能为空", groups = {CreateUser.class, UpdateUser.class})
        private String phone;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }


    interface CreateUser extends Default{

    }

    interface UpdateUser extends Default{

    }
}
