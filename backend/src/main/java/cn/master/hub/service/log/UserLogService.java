package cn.master.hub.service.log;

import cn.master.hub.constants.HttpMethodConstants;
import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.LogDTOBuilder;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
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
    public List<LogDTO> getBatchAddLogs(@Valid List<UserCreateInfo> userList, String operator, String requestPath) {
        List<LogDTO> logs = new ArrayList<>();
        userList.forEach(user -> {
            LogDTO log = LogDTOBuilder.builder()
                    .projectId(OperationLogConstants.SYSTEM)
                    .organizationId(OperationLogConstants.SYSTEM)
                    .type(OperationLogType.ADD.name())
                    .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                    .method(HttpMethodConstants.POST.name())
                    .path(requestPath)
                    .sourceId(user.getId())
                    .content(user.getName() + "(" + user.getEmail() + ")")
                    .originalValue(JacksonUtils.toJSONBytes(user))
                    .createUser(operator)
                    .build().getLogDTO();
            logs.add(log);
        });
        return logs;
    }
}
