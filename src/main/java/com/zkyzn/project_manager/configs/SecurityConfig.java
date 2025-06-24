// src/main/java/com/zkyzn/project_manager/configs/SecurityConfig.java
package com.zkyzn.project_manager.configs;

import com.zkyzn.project_manager.aspect.IgnoreAuthUrlRegistry;
import com.zkyzn.project_manager.filters.JwtTokenFilter;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.jwt.enabled:true}") // 默认值为true，如果yml中未配置则使用此默认值
    private boolean jwtSecurityEnabled;

    @Resource
    private JwtTokenFilter jwtTokenFilter; // 直接通过构造器注入
    @Resource
    private IgnoreAuthUrlRegistry ignoreAuthUrlRegistry;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter, IgnoreAuthUrlRegistry ignoreAuthUrlRegistry) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.ignoreAuthUrlRegistry = ignoreAuthUrlRegistry;
    }

    /**
     * 定义密码编码器Bean
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证基本配置
     *
     * @return 配置链路
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        if (jwtSecurityEnabled) {
            // 如果鉴权开启，则配置鉴权规则
            httpSecurity.authorizeHttpRequests(auth -> {
                        auth
                                .requestMatchers("/swagger-ui/**").permitAll() // 保持Swagger路径开放
                                .requestMatchers("/swagger-resources/**").permitAll()
                                .requestMatchers("/swagger-ui.html").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/token/login", "/api/token/register").permitAll(); // 允许登录和注册接口匿名访问

                        ignoreAuthUrlRegistry.getPermitAllUrls().forEach(url -> {
                            String[] parts = url.split(":", 2);
                            String httpMethod = parts[0];
                            String urlPattern = parts[1];

                            auth.requestMatchers(HttpMethod.valueOf(httpMethod), urlPattern).permitAll();
                        });
                        auth.anyRequest().authenticated();
                    }) // 其他所有请求都需要认证
                    .csrf(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class); // 使用注入的JwtTokenFilter
        } else {
            httpSecurity.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable); // HTTP Basic 通常也应该在完全放开时禁用

        return httpSecurity.build();
    }

}