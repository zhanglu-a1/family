package com.family.memoir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FamilyMemoirApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyMemoirApplication.class, args);
    }
}
