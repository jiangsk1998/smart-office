package com.zkyzn.project_manager;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.zkyzn.project_manager.mappers")
public class MainApplication {

    public static void main(String[] args) {
        //TODO 系统时间统一修改为 UTC+8 ,注意数据库，中间件等一致
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        SpringApplication.run(MainApplication.class, args);
    }
}
