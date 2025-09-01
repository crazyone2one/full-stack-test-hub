package cn.master.hub.service;

import cn.master.hub.dto.request.AuthenticationRequest;
import cn.master.hub.dto.response.AuthenticationResponse;
import cn.master.hub.dto.response.UserResponse;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.handler.JwtTokenProvider;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 2025/8/29
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticate);
        String accessToken = jwtTokenProvider.generateToken(authenticate, "access_token");
        String refreshToken = jwtTokenProvider.generateToken(authenticate, "refresh_token");
        UserResponse userResponse = QueryChain.of(SystemUser.class)
                .select(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.EMAIL, SYSTEM_USER.LAST_ORGANIZATION_ID, SYSTEM_USER.LAST_PROJECT_ID, SYSTEM_USER.AVATAR)
                .from(SYSTEM_USER).where(SYSTEM_USER.NAME.eq(authenticate.getName()))
                .oneAs(UserResponse.class);
        return new AuthenticationResponse(accessToken, refreshToken, userResponse);
    }
}
