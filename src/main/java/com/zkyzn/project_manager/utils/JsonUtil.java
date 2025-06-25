package com.zkyzn.project_manager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; // 导入 Autowired
import org.springframework.stereotype.Component; // 导入 Component

@Component // 将JsonUtil声明为Spring组件
public class JsonUtil {

    // 移除静态初始化块，通过Spring注入
    private static ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    // 通过setter方法注入ObjectMapper，确保Spring管理
    @Autowired
    public void setObjectMapper(ObjectMapper injectedObjectMapper) {
        JsonUtil.objectMapper = injectedObjectMapper;
        logger.info("ObjectMapper injected into JsonUtil. Hash: {}", injectedObjectMapper.hashCode());
    }

    public static ObjectMapper getObjectMapper() {
        // 提供一个公共方法来获取配置好的ObjectMapper实例
        // 在确保注入已完成的情况下使用
        if (objectMapper == null) {
            logger.warn("ObjectMapper in JsonUtil is null. This might indicate an issue with Spring context initialization or non-Spring usage.");
            // 因为它可能不会包含所有的自定义配置
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }
        return objectMapper;
    }

    /**
     * Serializes an object to its JSON string representation.
     * @param obj The object to serialize.
     * @return JSON string, or null if serialization fails.
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            // 使用通过Spring注入的ObjectMapper
            return getObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object to JSON: {}", obj.getClass().getName(), e);
            // Fallback to toString() should ideally be avoided for structured data.
            // Consider throwing a custom exception or returning null for clear error handling.
            return obj.toString();
        }
    }
}