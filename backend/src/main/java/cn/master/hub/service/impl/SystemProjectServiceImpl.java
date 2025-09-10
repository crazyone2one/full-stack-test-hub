package cn.master.hub.service.impl;

import cn.master.hub.constants.HttpMethodConstants;
import cn.master.hub.constants.InternalUserRole;
import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.ProjectAddMemberBatchRequest;
import cn.master.hub.dto.system.ProjectResourcePoolDTO;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.entity.*;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.invoker.ProjectServiceInvoker;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.ProjectTestResourcePoolMapper;
import cn.master.hub.mapper.SystemProjectMapper;
import cn.master.hub.mapper.UserRoleRelationMapper;
import cn.master.hub.service.OperationLogService;
import cn.master.hub.service.SystemProjectService;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.ProjectTestResourcePoolTableDef.PROJECT_TEST_RESOURCE_POOL;
import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.hub.entity.table.TestResourcePoolTableDef.TEST_RESOURCE_POOL;
import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-01
 */
@Service
@RequiredArgsConstructor
public class SystemProjectServiceImpl extends ServiceImpl<SystemProjectMapper, SystemProject> implements SystemProjectService {
    private final ProjectTestResourcePoolMapper projectTestResourcePoolMapper;
    private final ProjectServiceInvoker projectServiceInvoker;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final OperationLogService operationLogService;

    @Override
    public List<SystemProject> getUserProject(String organizationId, String currentUserName) {
        return queryChain().where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId)
                .and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO add(AddProjectRequest addProjectDTO, String createUser, String path, String module) {
        ProjectDTO projectDTO = new ProjectDTO();
        SystemProject systemProject = SystemProject.builder()
                .name(addProjectDTO.getName())
                .organizationId(addProjectDTO.getOrganizationId())
                .createUser(createUser)
                .updateUser(createUser)
                .enable(addProjectDTO.getEnable())
                .allResourcePool(addProjectDTO.isAllResourcePool())
                .description(addProjectDTO.getDescription())
                .build();
        checkProjectExistByName(systemProject);
        BeanUtils.copyProperties(systemProject, projectDTO);

        mapper.insert(systemProject);

        if (!addProjectDTO.getResourcePoolIds().isEmpty()) {
            checkResourcePoolExist(addProjectDTO.getResourcePoolIds());
            List<ProjectTestResourcePool> projectTestResourcePools = new ArrayList<>();
            projectTestResourcePoolMapper.deleteByCondition(PROJECT_TEST_RESOURCE_POOL.PROJECT_ID.eq(systemProject.getId()));
            addProjectDTO.getResourcePoolIds().forEach(resourcePoolId -> {
                ProjectTestResourcePool projectTestResourcePool = ProjectTestResourcePool.builder()
                        .projectId(systemProject.getId())
                        .testResourcePoolId(resourcePoolId)
                        .build();
                projectTestResourcePools.add(projectTestResourcePool);
            });
            projectTestResourcePoolMapper.insertBatch(projectTestResourcePools);
        }
        projectServiceInvoker.invokeCreateServices(systemProject.getId());
        ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
        memberRequest.setProjectIds(List.of(systemProject.getId()));
        memberRequest.setUserIds(addProjectDTO.getUserIds());
        addProjectAdmin(memberRequest, createUser, path, OperationLogType.ADD.name(), Translator.get("add"), module);
        return projectDTO;
    }

    @Override
    public Page<ProjectDTO> buildUserInfo(Page<ProjectDTO> page) {
        List<ProjectDTO> projectList = page.getRecords();
        List<String> projectIds = projectList.stream().map(ProjectDTO::getId).toList();
        List<UserExtendDTO> users = getProjectAdminList(projectIds);
        List<ProjectDTO> projectDTOList = getProjectExtendDTOList(projectIds);
        Map<String, ProjectDTO> projectMap = projectDTOList.stream().collect(Collectors.toMap(ProjectDTO::getId, projectDTO -> projectDTO));
        //根据sourceId分组
        Map<String, List<UserExtendDTO>> userMapList = users.stream().collect(Collectors.groupingBy(UserExtendDTO::getSourceId));
        //获取资源池
        List<ProjectResourcePoolDTO> projectResourcePoolDTOList = getProjectResourcePoolDTOList(projectIds);
        Map<String, List<ProjectResourcePoolDTO>> poolMap = projectResourcePoolDTOList.stream().collect(Collectors.groupingBy(ProjectResourcePoolDTO::getProjectId));
        projectList.forEach(projectDTO -> {
            projectDTO.setMemberCount(projectMap.get(projectDTO.getId()).getMemberCount());
            List<UserExtendDTO> userExtendDTOS = userMapList.get(projectDTO.getId());
            if (CollectionUtils.isNotEmpty(userExtendDTOS)) {
                projectDTO.setAdminList(userExtendDTOS);
                List<String> userIdList = userExtendDTOS.stream().map(SystemUser::getId).collect(Collectors.toList());
                projectDTO.setProjectCreateUserIsAdmin(CollectionUtils.isNotEmpty(userIdList) && userIdList.contains(projectDTO.getCreateUser()));
            } else {
                projectDTO.setAdminList(new ArrayList<>());
            }
            List<ProjectResourcePoolDTO> projectResourcePoolDTOS = poolMap.get(projectDTO.getId());
            if (CollectionUtils.isNotEmpty(projectResourcePoolDTOS)) {
                projectDTO.setResourcePoolList(projectResourcePoolDTOS);
            } else {
                projectDTO.setResourcePoolList(new ArrayList<>());
            }
        });
        return page;
    }

    private List<ProjectResourcePoolDTO> getProjectResourcePoolDTOList(List<String> projectIds) {
        return queryChain()
                .select(PROJECT_TEST_RESOURCE_POOL.PROJECT_ID, TEST_RESOURCE_POOL.ALL_COLUMNS)
                .from(PROJECT_TEST_RESOURCE_POOL).innerJoin(TEST_RESOURCE_POOL).on(PROJECT_TEST_RESOURCE_POOL.TEST_RESOURCE_POOL_ID.eq(TEST_RESOURCE_POOL.ID))
                .where(PROJECT_TEST_RESOURCE_POOL.PROJECT_ID.in(projectIds))
                .and(TEST_RESOURCE_POOL.ENABLE.eq(true))
                .listAs(ProjectResourcePoolDTO.class);
    }

    private List<ProjectDTO> getProjectExtendDTOList(List<String> projectIds) {
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                .select(USER_ROLE_RELATION.SOURCE_ID, SYSTEM_USER.ID)
                .from(USER_ROLE_RELATION).leftJoin(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.in(projectIds));
        QueryChain<SystemProject> projectQueryChain = queryChain()
                .select(SYSTEM_PROJECT.ID).select("count(distinct temp.id) as memberCount")
                .from(SYSTEM_PROJECT.as("p")).leftJoin(userRoleRelationQueryChain).as("temp")
                .on("p.id = temp.source_id")
                .groupBy("p.id");
        return mapper.selectListByQueryAs(projectQueryChain, ProjectDTO.class);
    }

    private List<UserExtendDTO> getProjectAdminList(List<String> projectIds) {
        return QueryChain.of(SystemUser.class)
                .select(SYSTEM_USER.ALL_COLUMNS, USER_ROLE_RELATION.SOURCE_ID)
                .from(USER_ROLE_RELATION).leftJoin(SYSTEM_USER)
                .on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))
                .and(USER_ROLE_RELATION.SOURCE_ID.in(projectIds))
                .listAs(UserExtendDTO.class);
    }

    private void addProjectAdmin(ProjectAddMemberBatchRequest request, String createUser, String path, String type, String content, String module) {
        List<OperationLog> logs = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        request.getProjectIds().forEach(projectId -> {
            SystemProject project = mapper.selectOneById(projectId);
            Map<String, String> nameMap = addUserPre(request.getUserIds(), createUser, path, module, projectId, project);
            request.getUserIds().forEach(userId -> {
                long count = QueryChain.of(UserRoleRelation.class).count();
                if (count == 0) {
                    UserRoleRelation adminRole = UserRoleRelation.builder()
                            .userId(userId)
                            .roleId(InternalUserRole.PROJECT_ADMIN.getValue())
                            .sourceId(projectId)
                            .organizationId(project.getOrganizationId())
                            .createUser(createUser)
                            .build();
                    userRoleRelations.add(adminRole);
                    String logProjectId = OperationLogConstants.SYSTEM;
                    if (Strings.CS.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                        logProjectId = OperationLogConstants.ORGANIZATION;
                    }
                    OperationLog operationLog = OperationLog.builder()
                            .projectId(logProjectId).organizationId(project.getOrganizationId())
                            .sourceId(adminRole.getId()).createUser(createUser).type(type)
                            .module(module).content(content + Translator.get("project_admin") + ": " + nameMap.get(userId)).build();
                    setLog(operationLog, path, HttpMethodConstants.POST.name(), logs);
                }
            });
        });
        if (!userRoleRelations.isEmpty()) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
        operationLogService.batchAdd(logs);
    }

    private Map<String, String> addUserPre(List<String> userIds, String createUser, String path, String module, String projectId, SystemProject project) {
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
        List<OperationLog> logs = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(UserRoleRelation::getUserId).in(userIds).list();
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
                    OperationLog operationLog = OperationLog.builder()
                            .projectId(orgId).organizationId(orgId)
                            .sourceId(relation.getId()).createUser(createUser).type(OperationLogType.ADD.name())
                            .module(module).content(Translator.get("add") + Translator.get("organization_member") + ": " + nameMap.get(userId)).build();
                    setLog(operationLog, path, HttpMethodConstants.POST.name(), logs);
                }
            });
            userRoleRelationMapper.insertBatch(userRoleRelation);
        }
        operationLogService.batchAdd(logs);
    }

    private void checkProjectNotExist(String projectId) {
        if (mapper.selectOneById(projectId) == null) {
            throw new CustomException(Translator.get("project_is_not_exist"));
        }
    }

    private void setLog(OperationLog operationLog, String path, String method, List<OperationLog> logs) {
        operationLog.setPath(path);
        operationLog.setMethod(method);
        operationLog.setOriginalValue(JacksonUtils.toJSONBytes(StringUtils.EMPTY));
        logs.add(operationLog);
    }

    private void checkResourcePoolExist(List<String> poolIds) {
        List<TestResourcePool> list = QueryChain.of(TestResourcePool.class).where(TestResourcePool::getId).in(poolIds)
                .and(TestResourcePool::getEnable).eq(true).list();
        if (poolIds.size() != list.size()) {
            throw new CustomException(Translator.get("resource_pool_not_exist"));
        }
    }

    private void checkProjectExistByName(SystemProject systemProject) {
        boolean exists = queryChain().where(SYSTEM_PROJECT.NAME.eq(systemProject.getName())
                .and(SYSTEM_PROJECT.ORGANIZATION_ID.eq(systemProject.getOrganizationId()))
                .and(SYSTEM_PROJECT.ID.ne(systemProject.getId()))).exists();
        if (exists) {
            throw new CustomException(Translator.get("project_name_already_exists"));
        }
    }
}
