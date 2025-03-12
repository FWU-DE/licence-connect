package com.fwu.lc_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@EnableJpaRepositories
public class LcCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(LcCoreApplication.class, args);
    }
}
