package com.zinemasterapp.zinemasterapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZineMasterAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZineMasterAppApplication.class, args);
    }

}
