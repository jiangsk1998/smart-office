package com.zkyzn.project_manager.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger的配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI configOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Api接口")
                        .description("Restful Api")
                        .version("v1.0.0")
                )
                .components(new Components()
                        .addSecuritySchemes("auth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .name("Authorization")
                                        .in(SecurityScheme.In.HEADER)
                        )
                );
    }
}
