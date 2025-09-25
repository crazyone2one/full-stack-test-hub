package cn.master.hub.service.log;

import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.system.OrganizationUserRoleEditRequest;
import cn.master.hub.dto.system.request.OrganizationUserRoleMemberEditRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.UserRoleMapper;
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
public class OrganizationUserRoleLogService {
    @Resource
    private UserRoleMapper userRoleMapper;
    public LogDTO addLog(OrganizationUserRoleEditRequest request) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.ORGANIZATION,
                request.getScopeId(),
                OperationLogConstants.SYSTEM,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.SETTING_ORGANIZATION_USER_ROLE,
                request.getName());

        dto.setOriginalValue(JacksonUtils.toJSONBytes(request.getName()));
        return dto;
    }

    public LogDTO updateLog(OrganizationUserRoleEditRequest request) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.ORGANIZATION,
                request.getScopeId(),
                OperationLogConstants.SYSTEM,
                null,
                OperationLogType.UPDATE.name(),
                OperationLogModule.SETTING_ORGANIZATION_USER_ROLE,
                request.getName());
        UserRole userRole = QueryChain.of(UserRole.class).where(UserRole::getId).eq(request.getId()).list().getFirst();
        dto.setOriginalValue(JacksonUtils.toJSONBytes(userRole.getName()));
        dto.setModifiedValue(JacksonUtils.toJSONBytes(request.getName()));
        return dto;
    }
    public LogDTO editMemberLog(OrganizationUserRoleMemberEditRequest request) {
        UserRole userRole = userRoleMapper.selectOneById(request.getUserRoleId());
        LogDTO dto = new LogDTO(
                OperationLogConstants.ORGANIZATION,
                request.getOrganizationId(),
                OperationLogConstants.SYSTEM,
                null,
                null,
                OperationLogModule.SETTING_ORGANIZATION_USER_ROLE,
                userRole.getName());
        dto.setType(OperationLogType.UPDATE.name());
        dto.setModifiedValue(JacksonUtils.toJSONBytes(request));
        return dto;
    }
    public LogDTO deleteLog(String id) {
        UserRole userRole = QueryChain.of(UserRole.class).where(UserRole::getId).eq(id).one();
        LogDTO dto = new LogDTO(
                OperationLogConstants.ORGANIZATION,
                userRole.getScopeId(),
                OperationLogConstants.SYSTEM,
                null,
                OperationLogType.DELETE.name(),
                OperationLogModule.SETTING_ORGANIZATION_USER_ROLE,
                userRole.getName());

        dto.setOriginalValue(JacksonUtils.toJSONBytes(userRole.getName()));
        return dto;
    }
}
