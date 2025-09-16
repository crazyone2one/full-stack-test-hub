package cn.master.hub.service.log;

import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.request.ProjectUserRoleEditRequest;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.entity.UserRole;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.SystemProjectMapper;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.query.QueryChain;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Created by 11's papa on 2025/9/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectUserRoleLogService {
    @Resource
    SystemProjectMapper systemProjectMapper;

    public LogDTO addLog(ProjectUserRoleEditRequest request) {
        SystemProject project = systemProjectMapper.selectOneById(request.getScopeId());
        LogDTO dto = new LogDTO(
                project.getId(),
                project.getOrganizationId(),
                OperationLogConstants.SYSTEM,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.PROJECT_MANAGEMENT_PERMISSION_USER_ROLE,
                request.getName());

        dto.setOriginalValue(JacksonUtils.toJSONBytes(request.getName()));
        return dto;
    }

    public LogDTO updateLog(ProjectUserRoleEditRequest request) {
        SystemProject project = systemProjectMapper.selectOneById(request.getScopeId());
        LogDTO dto = new LogDTO(
                project.getId(),
                project.getOrganizationId(),
                OperationLogConstants.SYSTEM,
                null,
                OperationLogType.UPDATE.name(),
                OperationLogModule.PROJECT_MANAGEMENT_PERMISSION_USER_ROLE,
                request.getName());

        UserRole userRole = QueryChain.of(UserRole.class).where(UserRole::getId).eq(request.getId()).list().getFirst();
        dto.setOriginalValue(JacksonUtils.toJSONBytes(userRole.getName()));
        dto.setModifiedValue(JacksonUtils.toJSONBytes(request.getName()));
        return dto;
    }

    public LogDTO deleteLog(String id) {
        UserRole userRole = QueryChain.of(UserRole.class).where(UserRole::getId).eq(id).one();
        SystemProject project = systemProjectMapper.selectOneById(userRole.getScopeId());
        LogDTO dto = new LogDTO(
                project.getId(),
                project.getOrganizationId(),
                OperationLogConstants.SYSTEM,
                null,
                OperationLogType.DELETE.name(),
                OperationLogModule.PROJECT_MANAGEMENT_PERMISSION_USER_ROLE,
                userRole.getName());

        dto.setOriginalValue(JacksonUtils.toJSONBytes(userRole.getName()));
        return dto;
    }
}
