package com.zkyzn.project_manager.configs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.slf4j.Logger; // 导入Logger
import org.slf4j.LoggerFactory; // 导入LoggerFactory

@Configuration
public class JsonConfig {

    private static final Logger logger = LoggerFactory.getLogger(JsonConfig.class); // 初始化Logger

    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(
                PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule()); // 显式注册 JavaTimeModule
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 添加日志，确认ObjectMapper配置是否生效
        logger.info("Custom ObjectMapper bean created. Hash: {}", objectMapper.hashCode());
        logger.info("DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES enabled: {}", objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        logger.info("SerializationFeature.WRITE_DATES_AS_TIMESTAMPS disabled: {}", !objectMapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

        return objectMapper;
    }
}