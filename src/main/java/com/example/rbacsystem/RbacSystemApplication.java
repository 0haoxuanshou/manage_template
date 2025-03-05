package com.example.rbacsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.rbacsystem.mapper")
public class RbacSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RbacSystemApplication.class, args);
    }

}