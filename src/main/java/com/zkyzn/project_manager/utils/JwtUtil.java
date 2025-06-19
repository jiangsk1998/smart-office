package com.zkyzn.project_manager.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private static RedisTemplate<String, Object> redisTemplate;

    // 使用构造器注入RedisTemplate
    public JwtUtil(RedisTemplate<String, Object> redisTemplate) {
        JwtUtil.redisTemplate = redisTemplate;
    }

    private static final String JWT_SECRET = "ZKYZN.Security"; // 保持与之前一致
    private static final long JWT_EXPIRATION_TIME_MS = 7 * 24 * 60 * 60 * 1000L; // 7天有效期

    /**
     * 获取JwtToken，并将其存储到Redis
     *
     * @param userId 用户Id
     * @return token
     */
    public static String createToken(Long userId) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MILLISECOND, (int) JWT_EXPIRATION_TIME_MS); // 设置过期时间

        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("userId", userId); // 将用户ID作为payload中的key，与后续SecurityUtil保持一致
        String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC384(JWT_SECRET));

        // 将token存储到Redis，并设置与JWT相同的过期时间
        // 这里以 "jwt:user:{userId}" 作为key，存储token，方便后续校验是否存在和快速查找
        redisTemplate.opsForValue().set("jwt:user:" + userId, token, JWT_EXPIRATION_TIME_MS, TimeUnit.MILLISECONDS);

        return token;
    }

    /**
     * 验证token，并检查Redis中是否存在
     *
     * @param token token
     * @return 验证信息
     */
    public static DecodedJWT verifyToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        try {
            // 验证JWT签名和过期时间
            JWTVerifier verifier = JWT.require(Algorithm.HMAC384(JWT_SECRET)).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // 从JWT中获取用户ID
            Long userId = decodedJWT.getClaim("userId").asLong();
            if (userId == null) {
                return null;
            }

            // 检查Redis中是否存在对应的token，确保是有效的会话
            String storedToken = (String) redisTemplate.opsForValue().get("jwt:user:" + userId);
            if (storedToken == null || !storedToken.equals(token)) {
                // Redis中不存在或与传入的token不匹配，说明token已失效（可能被登出或过期）
                return null;
            }

            return decodedJWT;
        } catch (JWTVerificationException e) {
            // JWT验证失败（如签名不匹配、过期）
            return null;
        }
    }

    /**
     * 使指定用户的JWT失效（用于登出）
     * @param userId 用户ID
     */
    public static void invalidateToken(Long userId) {
        if (userId != null) {
            redisTemplate.delete("jwt:user:" + userId);
        }
    }
}