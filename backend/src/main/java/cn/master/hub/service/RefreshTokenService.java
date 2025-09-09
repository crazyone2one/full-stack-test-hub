package cn.master.hub.service;

import cn.master.hub.entity.RefreshToken;
import com.mybatisflex.core.service.IService;

import java.util.Optional;

/**
 * refresh_token 服务层。
 *
 * @author 11's papa
 * @since 2025-09-03
 */
public interface RefreshTokenService extends IService<RefreshToken> {
    RefreshToken createRefreshToken(String username);

    RefreshToken verifyExpiration(RefreshToken token);

    Optional<RefreshToken> findByToken(String token);
}
