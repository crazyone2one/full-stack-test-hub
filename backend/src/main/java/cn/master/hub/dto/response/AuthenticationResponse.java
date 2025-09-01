package cn.master.hub.dto.response;

/**
 * @author Created by 11's papa on 2025/8/29
 */
public record AuthenticationResponse(String accessToken, String refreshToken, UserResponse user) {
}
