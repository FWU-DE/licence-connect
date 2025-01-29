package com.fwu.lc_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class LcCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(LcCoreApplication.class, args);
    }

}

@RestController
class HtmlController {
    @GetMapping("/hello")
    private String hello(String name) {
        String iName = name == null ? "World" : name;
        return "Hello, " + iName + "!";
    }
}