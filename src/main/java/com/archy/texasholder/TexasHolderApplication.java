package com.archy.texasholder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.archy.texasholder")
public class TexasHolderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TexasHolderApplication.class, args);
    }

}