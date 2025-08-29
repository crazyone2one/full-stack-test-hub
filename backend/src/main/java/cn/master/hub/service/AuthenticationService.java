package cn.master.hub.service;

import cn.master.hub.dto.request.AuthenticationRequest;
import cn.master.hub.entity.AuthenticationResponse;
import cn.master.hub.handler.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        return new AuthenticationResponse(accessToken, refreshToken);
    }
}
