package cn.master.hub.service.impl;

import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.constants.UserRoleEnum;
import cn.master.hub.constants.UserRoleScope;
import cn.master.hub.dto.response.UserTableResponse;
import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRoleRelation;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.UserRoleMapper;
import cn.master.hub.mapper.UserRoleRelationMapper;
import cn.master.hub.service.BaseUserRoleRelationService;
import cn.master.hub.service.OperationLogService;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.hub.constants.InternalUserRole.ADMIN;
import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.hub.entity.table.UserRoleTableDef.USER_ROLE;
import static cn.master.hub.handler.result.CommonResultCode.USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION;

/**
 * 用户组关系 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-09
 */
@Service
@RequiredArgsConstructor
public class BaseUserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation> implements BaseUserRoleRelationService {
    private final OperationLogService operationLogService;
    private final UserRoleMapper userRoleMapper;

    @Override
    public void deleteByRoleId(String roleId) {
        List<UserRoleRelation> userRoleRelations = getByRoleId(roleId);
        userRoleRelations.forEach(userRoleRelation ->
                checkAdminPermissionRemove(userRoleRelation.getUserId(), userRoleRelation.getRoleId()));
        mapper.deleteByCondition(USER_ROLE_RELATION.ROLE_ID.eq(roleId));
    }

    @Override
    public List<String> getUserIdByRoleId(String roleId) {
        return queryChain().select(USER_ROLE_RELATION.USER_ID).where(USER_ROLE_RELATION.ROLE_ID.eq(roleId)).listAs(String.class);
    }

    @Override
    public List<UserRoleRelation> getUserIdAndSourceIdByUserIds(List<String> userIds) {
        return queryChain().where(USER_ROLE_RELATION.USER_ID.in(userIds)).list();
    }

    @Override
    public void updateUserSystemGlobalRole(SystemUser user, String operator, List<String> roleList) {
        //更新用户权限
        List<String> deleteRoleList = new ArrayList<>();
        List<UserRoleRelation> saveList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelationList = selectGlobalRoleByUserId(user.getId());
        assert userRoleRelationList != null;
        List<String> userSavedRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).toList();
        //获取要移除的权限
        for (String userSavedRoleId : userSavedRoleIdList) {
            if (!roleList.contains(userSavedRoleId)) {
                deleteRoleList.add(userSavedRoleId);
            }
        }
        //获取要添加的权限
        for (String roleId : roleList) {
            if (!userSavedRoleIdList.contains(roleId)) {
                UserRoleRelation userRoleRelation = new UserRoleRelation();
                userRoleRelation.setUserId(user.getId());
                userRoleRelation.setRoleId(roleId);
                userRoleRelation.setSourceId(UserRoleScope.SYSTEM);
                userRoleRelation.setCreateUser(operator);
                userRoleRelation.setOrganizationId(UserRoleScope.SYSTEM);
                saveList.add(userRoleRelation);
            }
        }
        if (CollectionUtils.isNotEmpty(deleteRoleList)) {
            List<String> deleteIdList = new ArrayList<>();
            userRoleRelationList.forEach(item -> {
                if (deleteRoleList.contains(item.getRoleId())) {
                    deleteIdList.add(item.getId());
                }
            });
            mapper.deleteByQuery(queryChain().where(USER_ROLE_RELATION.ID.in(deleteIdList)));
            //记录删除日志
            operationLogService.batchAdd(getBatchLogs(deleteRoleList, user, "updateUser", operator, OperationLogType.DELETE.name()));
        }
        if (CollectionUtils.isNotEmpty(saveList)) {
            //系统级权限不会太多，所以暂时不分批处理
            saveList.forEach(item -> mapper.insert(item));
            //记录添加日志
            operationLogService.batchAdd(getBatchLogs(saveList.stream().map(UserRoleRelation::getRoleId).toList(),
                    user, "updateUser", operator, OperationLogType.ADD.name()));
        }
    }

    @Override
    public Map<String, UserTableResponse> selectGlobalUserRoleAndOrganization(List<String> userIdList) {
        List<UserRoleRelation> userRoleRelationList = queryChain().where(USER_ROLE_RELATION.USER_ID.in(userIdList)).list();
        List<String> userRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).distinct().collect(Collectors.toList());
        List<String> sourceIdList = userRoleRelationList.stream().map(UserRoleRelation::getSourceId).distinct().collect(Collectors.toList());
        Map<String, UserRole> userRoleMap = new HashMap<>();
        Map<String, SystemOrganization> organizationMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userRoleIdList)) {
            userRoleMap = QueryChain.of(UserRole.class)
                    .where(USER_ROLE.ID.in(userRoleIdList)).and(USER_ROLE.SCOPE_ID.eq(UserRoleEnum.GLOBAL.toString()))
                    .list().stream()
                    .collect(Collectors.toMap(UserRole::getId, item -> item));
        }
        if (CollectionUtils.isNotEmpty(sourceIdList)) {
            organizationMap = QueryChain.of(SystemOrganization.class)
                    .where(SystemOrganization::getId).in(sourceIdList)
                    .list().stream()
                    .collect(Collectors.toMap(SystemOrganization::getId, item -> item));
        }
        Map<String, UserTableResponse> returnMap = new HashMap<>();
        for (UserRoleRelation userRoleRelation : userRoleRelationList) {
            UserTableResponse userInfo = returnMap.get(userRoleRelation.getUserId());
            if (userInfo == null) {
                userInfo = new UserTableResponse();
                userInfo.setId(userRoleRelation.getUserId());
                returnMap.put(userRoleRelation.getUserId(), userInfo);
            }
            UserRole userRole = userRoleMap.get(userRoleRelation.getRoleId());
            if (userRole != null && Strings.CS.equals(userRole.getType(), UserRoleScope.SYSTEM)) {
                userInfo.setUserRole(userRole);
            }
            SystemOrganization organization = organizationMap.get(userRoleRelation.getSourceId());
            if (organization != null && !userInfo.getOrganizationList().contains(organization)) {
                userInfo.setOrganization(organization);
            }
        }
        return returnMap;
    }

    private List<UserRoleRelation> selectGlobalRoleByUserId(String userId) {
        List<String> userRoleIds = QueryChain.of(UserRole.class).select(USER_ROLE.ID)
                .from(USER_ROLE).where(USER_ROLE.TYPE.eq("SYSTEM")
                        .and(USER_ROLE.SCOPE_ID.eq("global")))
                .listAs(String.class);
        return queryChain().select(USER_ROLE_RELATION.ALL_COLUMNS)
                .from(USER_ROLE_RELATION).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                        .and(USER_ROLE_RELATION.ROLE_ID.in(userRoleIds)))
                .list();
    }

    private List<LogDTO> getBatchLogs(@Valid @NotEmpty List<String> userRoleId,
                                      @Valid SystemUser user,
                                      @Valid @NotEmpty String operationMethod,
                                      @Valid @NotEmpty String operator,
                                      @Valid @NotEmpty String operationType) {
        List<LogDTO> logs = new ArrayList<>();
        List<UserRole> userRoleList = QueryChain.of(UserRole.class).where(UserRole::getId).in(userRoleId).list();
        userRoleList.forEach(userRole -> {
            LogDTO log = new LogDTO();
            log.setProjectId(OperationLogConstants.SYSTEM);
            log.setOrganizationId(OperationLogConstants.SYSTEM);
            log.setType(operationType);
            log.setCreateUser(operator);
            log.setModule(OperationLogModule.SETTING_SYSTEM_USER_SINGLE);
            log.setMethod(operationMethod);
            log.setSourceId(user.getId());
            log.setContent(user.getName() + StringUtils.SPACE
                    + Translator.get(StringUtils.lowerCase(operationType)) + StringUtils.SPACE
                    + Translator.get("permission.project_group.name") + StringUtils.SPACE
                    + userRole.getName());
            log.setOriginalValue(JacksonUtils.toJSONBytes(userRole));
            logs.add(log);
        });
        return logs;
    }

    private void checkAdminPermissionRemove(String userId, String roleId) {
        if (Strings.CS.equals(roleId, ADMIN.getValue()) && Strings.CS.equals(userId, ADMIN.getValue())) {
            throw new CustomException(USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION);
        }
    }

    private List<UserRoleRelation> getByRoleId(String roleId) {
        return queryChain().where(USER_ROLE_RELATION.ROLE_ID.eq(roleId)).list();
    }
}
