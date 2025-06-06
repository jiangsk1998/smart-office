package com.zkyzn.project_manager.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

public class JwtUtil {
    /**
     * 获取JwtToken
     *
     * @param UserId 用户Id
     * @return token
     */
    public static String getJwtToken(Integer UserId) {
        Calendar instance = Calendar.getInstance();
        // 7天有效
        instance.add(Calendar.DATE, 7);

        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("id", UserId);
        return builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC384("ZKYZN.Security"));
    }

    /**
     * 验证token
     *
     * @param token token
     * @return 验证信息
     */
    public static DecodedJWT verify(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        JWTVerifier verifier = JWT.require(Algorithm.HMAC384("ZKYZN.Security")).build();
        return verifier.verify(token);
    }
}
