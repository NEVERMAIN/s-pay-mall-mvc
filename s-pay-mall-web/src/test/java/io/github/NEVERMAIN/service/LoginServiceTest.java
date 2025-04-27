package io.github.NEVERMAIN.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
public class LoginServiceTest {

    @Resource
    private ILoginService loginService;

    @Test
    public void testCreateTicket() throws InterruptedException, IOException {
        String ticket = loginService.createTicket();

        new CountDownLatch(1).await();

    }


}
