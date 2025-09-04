package cn.master.hub.service;

import cn.master.hub.dto.request.AuthenticationRequest;
import cn.master.hub.dto.request.RefreshTokenRequest;
import cn.master.hub.dto.response.AuthenticationResponse;
import cn.master.hub.dto.response.UserResponse;
import cn.master.hub.entity.RefreshToken;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.handler.JwtTokenProvider;
import cn.master.hub.handler.result.ResultHolder;
import com.mybatisflex.core.query.QueryChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 2025/8/29
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticate);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.username());
        String accessToken = jwtTokenProvider.generateToken(request.username(), "access_token");
//        String refreshToken = jwtTokenProvider.generateToken(authenticate, "refresh_token");
        UserResponse userResponse = QueryChain.of(SystemUser.class)
                .select(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.EMAIL, SYSTEM_USER.LAST_ORGANIZATION_ID, SYSTEM_USER.LAST_PROJECT_ID, SYSTEM_USER.AVATAR)
                .from(SYSTEM_USER).where(SYSTEM_USER.NAME.eq(authenticate.getName()))
                .oneAs(UserResponse.class);
        return new AuthenticationResponse(accessToken, refreshToken.getToken(), userResponse);
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserId)
                .map(userId -> {
                    String accessToken = jwtTokenProvider.generateToken(userId, "access_token");
                    return new AuthenticationResponse(accessToken, refreshTokenRequest.refreshToken(), null);
                }).orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
    }

    public ResultHolder logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            // Logs out the user by clearing their authentication info from the SecurityContext
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        tokenBlacklistService.addToBlacklist(request);
        return ResultHolder.success("Logout successful");
    }
}
