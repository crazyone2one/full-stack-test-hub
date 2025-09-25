package cn.master.hub.service.log;

import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.system.request.GlobalUserRoleRelationUpdateRequest;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRoleRelation;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.UserRoleMapper;
import cn.master.hub.mapper.UserRoleRelationMapper;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.query.QueryChain;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * @author Created by 11's papa on 2025/9/24
 */
@Service
public class GlobalUserRoleRelationLogService {
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private UserRoleRelationMapper userRoleRelationMapper;

    public LogDTO addLog(GlobalUserRoleRelationUpdateRequest request) {
        UserRole userRole = userRoleMapper.selectOneById(request.getRoleId());
        List<String> userIds = request.getUserIds();
        List<OptionDTO> users = QueryChain.of(SystemUser.class)
                .select(SYSTEM_USER.ID.as("value"), SYSTEM_USER.NAME.as("label"))
                .from(SYSTEM_USER).where(SYSTEM_USER.ID.in(userIds))
                .listAs(OptionDTO.class);
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                userRole.getId(),
                null,
                OperationLogType.UPDATE.name(),
                OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                userRole.getName());

        dto.setOriginalValue(JacksonUtils.toJSONBytes(users));
        return dto;
    }

    public LogDTO deleteLog(String id) {
        UserRoleRelation userRoleRelation = userRoleRelationMapper.selectOneById(id);
        UserRole userRole = userRoleMapper.selectOneById(userRoleRelation.getRoleId());
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                userRole.getId(),
                null,
                OperationLogType.UPDATE.name(),
                OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                userRole.getName());

        SystemUser userDTO = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.eq(userRoleRelation.getUserId())).one();
        OptionDTO optionDTO = new OptionDTO();
        optionDTO.setValue(userDTO.getId());
        optionDTO.setLabel(userDTO.getName());
        // 记录用户id和name
        dto.setOriginalValue(JacksonUtils.toJSONBytes(optionDTO));
        return dto;
    }
}
