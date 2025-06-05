package com.zkyzn.project_manager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@MapperScan("com.zkyzn.project_manager.mappers")
public class MainApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));
        SpringApplication.run(MainApplication.class, args);
    }
}
