package cn.master.hub.service.impl;

import cn.master.hub.handler.JwtTokenProvider;
import cn.master.hub.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Created by 11's papa on 2025/9/4
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void addToBlacklist(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Date expiry = jwtTokenProvider.extractExpiration(token);
        // Calculate the remaining time to expiration
        long expiration = expiry.getTime() - System.currentTimeMillis();
        assert token != null;
        redisTemplate.opsForValue().set(token, "blacklisted", expiration, TimeUnit.MILLISECONDS);
    }

    @Override
    public Boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // Get the Authorization header from the request
        String authorizationHeader = request.getHeader("Authorization");

        // Check if the Authorization header is not null and starts with "Bearer "
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            // Extract the JWT token (remove "Bearer " prefix)
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
