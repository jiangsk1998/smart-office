package com.zkyzn.project_manager.configs;

import com.zkyzn.project_manager.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity()
public class SecurityConfig {

    /**
     * 认证基本配置
     * @return 配置链路
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/swagger-ui/**").permitAll()
//                        .requestMatchers("/swagger-resources/**").permitAll()
//                        .requestMatchers("/swagger-ui.html").permitAll()
//                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterAfter(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
