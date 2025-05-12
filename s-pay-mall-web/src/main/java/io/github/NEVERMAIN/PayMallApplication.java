package io.github.NEVERMAIN;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PayMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayMallApplication.class, args);
    }

}
