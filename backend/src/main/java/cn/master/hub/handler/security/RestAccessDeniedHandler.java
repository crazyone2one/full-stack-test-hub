package cn.master.hub.handler.security;

import cn.master.hub.handler.result.ResultHolder;
import cn.master.hub.util.JacksonUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Created by 11's papa on 2025/8/29
 */
@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error("Access denied error: {}", accessDeniedException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ResultHolder error = ResultHolder.error(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage(), request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(JacksonUtils.toJSONString(error));
    }
}
