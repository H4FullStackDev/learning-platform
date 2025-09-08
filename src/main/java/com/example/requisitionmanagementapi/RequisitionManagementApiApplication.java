package com.example.requisitionmanagementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RequisitionManagementApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequisitionManagementApiApplication.class, args);
    }

}
