package cn.master.hub.service;

import cn.master.hub.constants.HttpMethodConstants;
import cn.master.hub.constants.InternalUserRole;
import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.request.ProjectAddMemberRequest;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRoleRelation;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.SystemProjectMapper;
import cn.master.hub.mapper.UserRoleRelationMapper;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * @author Created by 11's papa on 2025/9/19
 */
@Service
@RequiredArgsConstructor
public class CommonProjectService {
    private final SystemProjectMapper projectMapper;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final OperationLogService operationLogService;

    public void addProjectUser(ProjectAddMemberRequest request, String createUser, String path, String type, String content, String module) {
        List<LogDTO> logDTOList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        SystemProject project = projectMapper.selectOneById(request.getProjectId());
        Map<String, String> userMap = addUserPre(request.getUserIds(), createUser, path, module, request.getProjectId(), project);
        request.getUserIds().forEach(userId -> {
            request.getUserRoleIds().forEach(userRoleId -> {
                UserRoleRelation userRoleRelation = new UserRoleRelation();
                userRoleRelation.setUserId(userId);
                userRoleRelation.setSourceId(request.getProjectId());
                userRoleRelation.setRoleId(userRoleId);
                userRoleRelation.setCreateUser(createUser);
                userRoleRelation.setOrganizationId(project.getOrganizationId());
                userRoleRelations.add(userRoleRelation);
                String logProjectId = OperationLogConstants.SYSTEM;
                if (Strings.CS.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                    logProjectId = OperationLogConstants.ORGANIZATION;
                }
                LogDTO logDTO = new LogDTO(logProjectId, OperationLogConstants.SYSTEM, userRoleRelation.getId(), createUser, type, module, content + Translator.get("project_member") + ": " + userMap.get(userId));
                setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
            });
        });
        if (CollectionUtils.isNotEmpty(userRoleRelations)) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
        operationLogService.batchAdd(logDTOList);
    }

    public void setLog(LogDTO operationLog, String path, String method, List<LogDTO> logs) {
        operationLog.setPath(path);
        operationLog.setMethod(method);
        operationLog.setOriginalValue(JacksonUtils.toJSONBytes(StringUtils.EMPTY));
        logs.add(operationLog);
    }

    public Map<String, String> addUserPre(List<String> userIds, String createUser, String path, String module, String projectId, SystemProject project) {
        checkProjectNotExist(projectId);
        List<SystemUser> users = QueryChain.of(SystemUser.class).where(SystemUser::getId).in(userIds).list();
        if (userIds.size() != users.size()) {
            throw new CustomException(Translator.get("user_not_exist"));
        }
        //把id和名称放一个map中
        Map<String, String> userMap = users.stream().collect(Collectors.toMap(SystemUser::getId, SystemUser::getName));
        checkOrgRoleExit(userIds, project.getOrganizationId(), createUser, userMap, path, module);
        return userMap;
    }

    private void checkOrgRoleExit(List<String> userIds, String orgId, String createUser, Map<String, String> nameMap, String path, String module) {
        List<LogDTO> logs = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class)
                .where(UserRoleRelation::getUserId).in(userIds)
                .and(UserRoleRelation::getSourceId).eq(orgId)
                .list();
        //把用户id放到一个新的list
        List<String> orgUserIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
        if (!userIds.isEmpty()) {
            List<UserRoleRelation> userRoleRelation = new ArrayList<>();
            userIds.forEach(userId -> {
                if (!orgUserIds.contains(userId)) {
                    UserRoleRelation relation = UserRoleRelation.builder()
                            .userId(userId).roleId(InternalUserRole.ORG_MEMBER.getValue())
                            .sourceId(orgId).createUser(createUser).organizationId(orgId)
                            .build();
                    userRoleRelations.add(relation);
                    LogDTO logDTO = new LogDTO(orgId, orgId, relation.getId(), createUser, OperationLogType.ADD.name(), module, Translator.get("add") + Translator.get("organization_member") + ": " + nameMap.get(userId));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logs);
                }
            });
            if (CollectionUtils.isNotEmpty(userRoleRelation)) {
                userRoleRelationMapper.insertBatch(userRoleRelation);
            }
        }
        operationLogService.batchAdd(logs);
    }

    private void checkProjectNotExist(String projectId) {
        if (projectMapper.selectOneById(projectId) == null) {
            throw new CustomException(Translator.get("project_is_not_exist"));
        }
    }

    public int removeProjectMember(String projectId, String userId, String createUser, String module, String path) {
        checkProjectNotExist(projectId);
        SystemUser user = QueryChain.of(SystemUser.class).where(SystemUser::getId).eq(userId).one();
        if (user == null) {
            throw new CustomException(Translator.get("user_not_exist"));
        }
        //判断用户是不是最后一个管理员  如果是  就报错
        long count = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(projectId)
                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))).count();
        if (count == 0) {
            throw new CustomException(Translator.get("keep_at_least_one_administrator"));
        }

        if (Strings.CS.equals(projectId, user.getLastProjectId())) {
            UpdateChain.of(SystemUser.class).set(SystemUser::getLastProjectId, StringUtils.EMPTY).where(SystemUser::getId).eq(userId).update();
        }
        List<LogDTO> logDTOList = new ArrayList<>();
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(projectId).and(USER_ROLE_RELATION.USER_ID.eq(userId)));
        userRoleRelationQueryChain
                .list().forEach(userRoleRelation -> {
                    String logProjectId = OperationLogConstants.SYSTEM;
                    if (Strings.CS.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                        logProjectId = OperationLogConstants.ORGANIZATION;
                    }
                    LogDTO logDTO = new LogDTO(logProjectId, OperationLogConstants.SYSTEM, userRoleRelation.getId(), createUser, OperationLogType.DELETE.name(), module, Translator.get("delete") + Translator.get("project_member") + ": " + user.getName());
                    setLog(logDTO, path, HttpMethodConstants.GET.name(), logDTOList);
                });
        operationLogService.batchAdd(logDTOList);
        return userRoleRelationMapper.deleteByQuery(userRoleRelationQueryChain);
    }
}
