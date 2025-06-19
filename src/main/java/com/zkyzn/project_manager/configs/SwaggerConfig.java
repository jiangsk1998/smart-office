package com.zkyzn.project_manager.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication"; // 定义安全方案的名称

        return new OpenAPI()
                .info(new Info().title("项目管理系统API").version("1.0").description("项目管理系统API文档"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) // 将安全方案添加到全局安全要求中
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme() // 定义Bearer Token安全方案
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP) // HTTP认证方案
                                .scheme("bearer") // 使用bearer模式
                                .bearerFormat("JWT") // Bearer Token的格式是JWT
                                .description("请输入 JWT Token，例如：Bearer eyJhbGciOiJIUzI1NiJ9...") // 提示信息
                        )
                );
    }
}