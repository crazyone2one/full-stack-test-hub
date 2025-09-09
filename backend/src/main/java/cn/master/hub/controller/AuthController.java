package cn.master.hub.controller;

import cn.master.hub.dto.UserDTO;
import cn.master.hub.dto.request.AuthenticationRequest;
import cn.master.hub.dto.request.RefreshTokenRequest;
import cn.master.hub.dto.response.AuthenticationResponse;
import cn.master.hub.handler.result.ResultCode;
import cn.master.hub.handler.result.ResultHolder;
import cn.master.hub.handler.security.UserPrincipal;
import cn.master.hub.service.AuthenticationService;
import cn.master.hub.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Created by 11's papa on 2025/8/29
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @Value("${spring.messages.default-locale}")
    private String defaultLocale;
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResultHolder logout(HttpServletRequest request, HttpServletResponse response) {
        return authenticationService.logout(request, response);
    }

    @GetMapping(value = "/is-login")
    @Operation(summary = "是否登录")
    public ResultHolder isLogin(HttpServletResponse response) {
        UserPrincipal currentUser = SessionUtils.getCurrentUser();
        if (currentUser != null) {
            UserDTO userDTO = authenticationService.getUserDTO(currentUser.user().getId());
            if (StringUtils.isBlank(userDTO.getLanguage())) {
                userDTO.setLanguage(defaultLocale.replace("_", "-"));
            }
            authenticationService.autoSwitch(userDTO);
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResultHolder.error(ResultCode.UNAUTHORIZED.getCode(), null);
    }
}
