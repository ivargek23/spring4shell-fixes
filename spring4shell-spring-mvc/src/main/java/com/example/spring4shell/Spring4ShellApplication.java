package com.example.spring4shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Spring4ShellApplication {

    public static void main(String[] args) {
        SpringApplication.run(Spring4ShellApplication.class, args);
    }
}
