package com.zkyzn.project_manager.aspect;

import com.zkyzn.project_manager.annotation.IgnoreAuth;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 忽略鉴权路径注册器
 * @author Zhang Fan
 */
@Configuration
public class IgnoreAuthUrlRegistry {

    private final RequestMappingHandlerMapping handlerMapping;
    private final Set<String> permitAllUrls = new HashSet<>();

    @Autowired
    public IgnoreAuthUrlRegistry(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @PostConstruct
    public void init() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        handlerMethods.forEach((handlerMapping, handlerMethod) -> {
            IgnoreAuth methodAnnotation = handlerMethod.getMethodAnnotation(IgnoreAuth.class);
            IgnoreAuth typeAnnotation = handlerMethod.getBeanType().getAnnotation(IgnoreAuth.class);
            if (methodAnnotation != null || typeAnnotation != null) {
                handlerMapping.getPathPatternsCondition().getPatterns().forEach(pattern -> {
                    handlerMapping.getMethodsCondition().getMethods().forEach(method -> {
                        permitAllUrls.add(method.name() + ":" + pattern);
                    });
                });
            }
        });
    }

    public Set<String> getPermitAllUrls() {
        return permitAllUrls;
    }
}
