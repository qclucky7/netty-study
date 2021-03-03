package com.netty.study;

import com.netty.study.springeventlisten.user.UserService;
import com.netty.study.springstrategymodel.PayStrategy;
import com.netty.study.springstrategymodel.SourceType;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class StudyApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private PayStrategy payStrategy;

    @Test
    public void testEvent(){
        userService.registerUser();
    }

    @Test
    public void testStrategy(){
        payStrategy.on(SourceType.ALIPAY);
        payStrategy.on(SourceType.WECHATE);
    }


}
