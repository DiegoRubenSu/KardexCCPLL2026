package com.kardex.kardex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KardexApplication {
    public static void main(String[] args) {
        SpringApplication.run(KardexApplication.class, args);
    }
}