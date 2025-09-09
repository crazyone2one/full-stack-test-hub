package cn.master.hub.service.impl;

import cn.master.hub.entity.RefreshToken;
import cn.master.hub.mapper.RefreshTokenMapper;
import cn.master.hub.service.RefreshTokenService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * refresh_token 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-03
 */
@Service
public class RefreshTokenServiceImpl extends ServiceImpl<RefreshTokenMapper, RefreshToken> implements RefreshTokenService {
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long REFRESH_TOKEN_EXPIRATION;

    @Override
    public RefreshToken createRefreshToken(String username) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION))  // refresh token is valid for 60 mins
                .userId(username)
                .build();
        mapper.insert(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            mapper.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return queryChain().where(RefreshToken::getToken).eq(token).oneOpt();
    }
}
