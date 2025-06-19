package com.zkyzn.project_manager.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zkyzn.project_manager.utils.JwtUtil;
import com.zkyzn.project_manager.utils.security.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的指定的值
        String headerToken = request.getHeader("Authorization");
        
        // 保证 header中的 token 不为 null 且以 "Bearer " 开头
        if (headerToken != null && headerToken.startsWith("Bearer ")) {
            String jwtToken = headerToken.substring(7); // 提取JWT部分

            // 判断 UserDetails 中的用户主体是否为null (即当前请求尚未被认证)
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                DecodedJWT decodedJWT = JwtUtil.verifyToken(jwtToken); // 调用更新后的verifyToken
                
                if (decodedJWT != null) {
                    // 从JWT中获取用户ID
                    Long userId = decodedJWT.getClaim("userId").asLong(); // 注意这里是 "userId"
                    
                    if (userId != null) {
                        // 构建UserDetails对象
                        // 这里简化处理，可以根据实际业务从数据库加载用户权限等信息
                        UserDetails userDetails = CurrentUser.withUsername(String.valueOf(userId))
                                .adminId(userId.intValue()) // CurrentUser构造器需要Integer，这里转换一下
                                .roles("USER") // 赋予默认角色
                                .build();
                        
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        
                        // 将 authenticationToken 设置到 SecurityContext 中
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}