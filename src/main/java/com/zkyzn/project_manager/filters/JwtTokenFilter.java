package com.zkyzn.project_manager.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.zkyzn.project_manager.utils.JwtUtils;
import com.zkyzn.project_manager.utils.security.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的指定的值
        String headerToken = request.getHeader("Authorization");
        // 保证 header中的 token 不为 null
        if (headerToken!=null){
            // 判断 UserDetails 中的用户主体是否为null
            if (SecurityContextHolder.getContext().getAuthentication() == null){
                DecodedJWT decodedJWT = JwtUtils.verify(headerToken);
                assert decodedJWT != null;
                Integer id = decodedJWT.getClaim("id").asInt();
                UserDetails userDetails = CurrentUser.withUsername(String.valueOf(id)).adminId(id).roles("USER").build();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                // 将 authenticationToken 设置到 SecurityContext 中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
