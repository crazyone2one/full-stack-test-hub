package cn.master.hub.dto.response;

import cn.master.hub.dto.UserDTO;

/**
 * @author Created by 11's papa on 2025/8/29
 */
public record AuthenticationResponse(String accessToken, String refreshToken, UserDTO user) {
}
