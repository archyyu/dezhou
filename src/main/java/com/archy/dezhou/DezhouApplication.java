package com.archy.dezhou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.archy.dezhou")
public class DezhouApplication {

    public static void main(String[] args) {
        SpringApplication.run(DezhouApplication.class, args);
    }

}