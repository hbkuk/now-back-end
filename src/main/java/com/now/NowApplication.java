package com.now;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.now.core")
public class NowApplication {

    public static void main(String[] args) {
        SpringApplication.run(NowApplication.class, args);
    }

}
