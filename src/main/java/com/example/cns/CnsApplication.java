package com.example.cns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CnsApplication.class, args);
    }

}
