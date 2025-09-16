package cn.master.hub.service.log;

import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.dto.system.UserRoleUpdateRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.UserRoleMapper;
import cn.master.hub.util.JacksonUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Created by 11's papa on 2025/9/15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GlobalUserRoleLogService {
    @Resource
    UserRoleMapper userRoleMapper;

    public LogDTO addLog(UserRoleUpdateRequest request) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                null,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                request.getName());

        dto.setOriginalValue(JacksonUtils.toJSONBytes(request));
        return dto;
    }

    public LogDTO updateLog(UserRoleUpdateRequest request) {
        UserRole userRole = userRoleMapper.selectOneById(request.getId());
        LogDTO dto = null;
        if (userRole != null) {
            dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    userRole.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                    userRole.getName());

            dto.setOriginalValue(JacksonUtils.toJSONBytes(userRole));
        }
        return dto;
    }

    public LogDTO updateLog(PermissionSettingUpdateRequest request) {
        UserRole userRole = userRoleMapper.selectOneById(request.getUserRoleId());
        LogDTO dto = null;
        if (userRole != null) {
            dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    request.getUserRoleId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                    userRole.getName());

            dto.setOriginalValue(JacksonUtils.toJSONBytes(request));
        }
        return dto;
    }

    public LogDTO deleteLog(String id) {
        UserRole userRole = userRoleMapper.selectOneById(id);
        if (userRole == null) {
            return null;
        }
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                userRole.getId(),
                null,
                OperationLogType.DELETE.name(),
                OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                userRole.getName());

        dto.setOriginalValue(JacksonUtils.toJSONBytes(userRole));
        return dto;
    }
}
