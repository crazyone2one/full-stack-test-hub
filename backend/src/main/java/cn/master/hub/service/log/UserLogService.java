package cn.master.hub.service.log;

import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.entity.OperationLog;
import cn.master.hub.util.JacksonUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/4
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserLogService {
    public List<OperationLog> getBatchAddLogs(@Valid List<UserCreateInfo> userList, String operator, String requestPath) {
        List<OperationLog> logs = new ArrayList<>();
        userList.forEach(user -> {
            OperationLog log = OperationLog.builder()
                    .projectId("SYSTEM")
                    .organizationId("SYSTEM")
                    .type("ADD")
                    .module("SETTING_SYSTEM_USER_SINGLE")
                    .method("POST")
                    .path(requestPath)
                    .sourceId(user.getId())
                    .content(user.getName() + "(" + user.getEmail() + ")")
                    .originalValue(JacksonUtils.toJSONBytes(user))
                    .createUser(operator)
                    .build();
            logs.add(log);
        });
        return logs;
    }
}
