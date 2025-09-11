package cn.master.hub.service.impl;

import cn.master.hub.constants.HttpMethodConstants;
import cn.master.hub.constants.InternalUserRole;
import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.ProjectAddMemberBatchRequest;
import cn.master.hub.dto.system.ProjectResourcePoolDTO;
import cn.master.hub.dto.system.UpdateProjectRequest;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.entity.*;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.invoker.ProjectServiceInvoker;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.ProjectTestResourcePoolMapper;
import cn.master.hub.mapper.SystemProjectMapper;
import cn.master.hub.mapper.SystemUserMapper;
import cn.master.hub.mapper.UserRoleRelationMapper;
import cn.master.hub.service.OperationLogService;
import cn.master.hub.service.SystemProjectService;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
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
    private final SystemUserMapper systemUserMapper;

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
                .projectCode(addProjectDTO.getProjectCode())
                .organizationId(addProjectDTO.getOrganizationId())
                .createUser(createUser)
                .updateUser(createUser)
                .enable(addProjectDTO.getEnable())
                .allResourcePool(addProjectDTO.isAllResourcePool())
                .description(addProjectDTO.getDescription())
                .build();
        checkProjectExistByName(systemProject);
        if (CollectionUtils.isNotEmpty(addProjectDTO.getModuleIds())) {
            systemProject.setModuleSetting(addProjectDTO.getModuleIds());
        }
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO update(UpdateProjectRequest request, String updateUser, String path, String module) {
        SystemProject project = new SystemProject();
        ProjectDTO projectDTO = new ProjectDTO();
        project.setId(request.getId());
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOrganizationId(request.getOrganizationId());
        project.setEnable(request.getEnable());
        project.setAllResourcePool(request.isAllResourcePool());
        project.setUpdateUser(updateUser);
        checkProjectExistByName(project);
        checkProjectNotExist(project.getId());
        BeanUtils.copyProperties(project, projectDTO);
        if (CollectionUtils.isNotEmpty(request.getResourcePoolIds())) {
            checkResourcePoolExist(request.getResourcePoolIds());
            List<ProjectTestResourcePool> projectTestResourcePools = new ArrayList<>();
            projectTestResourcePoolMapper.deleteByCondition(PROJECT_TEST_RESOURCE_POOL.PROJECT_ID.eq(project.getId()));
            request.getResourcePoolIds().forEach(resourcePoolId -> {
                ProjectTestResourcePool projectTestResourcePool = ProjectTestResourcePool.builder()
                        .projectId(project.getId())
                        .testResourcePoolId(resourcePoolId)
                        .build();
                projectTestResourcePools.add(projectTestResourcePool);
            });
            projectTestResourcePoolMapper.insertBatch(projectTestResourcePools);
        } else {
            projectTestResourcePoolMapper.deleteByCondition(PROJECT_TEST_RESOURCE_POOL.PROJECT_ID.eq(project.getId()));
        }
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(project.getId())
                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))).list();
        List<String> orgUserIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
        List<LogDTO> logDTOList = new ArrayList<>();
        List<String> deleteIds = orgUserIds.stream().filter(item -> !request.getUserIds().contains(item)).toList();
        List<String> insertIds = request.getUserIds().stream().filter(item -> !orgUserIds.contains(item)).toList();
        if (CollectionUtils.isNotEmpty(deleteIds)) {
            QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(project.getId())
                    .and(USER_ROLE_RELATION.USER_ID.in(deleteIds))
                    .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue())));
            queryChain.list().forEach(userRoleRelation -> {
                SystemUser user = systemUserMapper.selectOneById(userRoleRelation.getUserId());
                String logProjectId = OperationLogConstants.SYSTEM;
                if (Strings.CS.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                    logProjectId = OperationLogConstants.ORGANIZATION;
                }
                LogDTO logDTO = new LogDTO(logProjectId, project.getOrganizationId(), userRoleRelation.getId(), updateUser, OperationLogType.DELETE.name(), module, Translator.get("delete") + Translator.get("project_admin") + ": " + user.getName());
                setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
            });
            userRoleRelationMapper.deleteByQuery(queryChain);
        }
        if (CollectionUtils.isNotEmpty(insertIds)) {
            ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
            memberRequest.setProjectIds(List.of(project.getId()));
            memberRequest.setUserIds(insertIds);
            addProjectAdmin(memberRequest, updateUser, path, OperationLogType.ADD.name(), Translator.get("add"), module);
        }
        if (CollectionUtils.isNotEmpty(logDTOList)) {
            operationLogService.batchAdd(logDTOList);
        }
        mapper.update(project);
        return projectDTO;
    }

    @Override
    public int delete(String id, String deleteUser) {
        checkProjectNotExist(id);
        return mapper.deleteById(id);
    }

    @Override
    public void enable(String id, String updateUser) {
        checkProjectNotExist(id);
        UpdateChain.of(SystemProject.class).set(SYSTEM_PROJECT.ENABLE, true)
                .set(SYSTEM_PROJECT.UPDATE_USER, updateUser)
                .where(SYSTEM_PROJECT.ID.eq(id)).update();
    }

    @Override
    public void disable(String id, String updateUser) {
        checkProjectNotExist(id);
        UpdateChain.of(SystemProject.class).set(SYSTEM_PROJECT.ENABLE, false)
                .set(SYSTEM_PROJECT.UPDATE_USER, updateUser)
                .where(SYSTEM_PROJECT.ID.eq(id)).update();
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
        List<LogDTO> logs = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        request.getProjectIds().forEach(projectId -> {
            SystemProject project = mapper.selectOneById(projectId);
            Map<String, String> nameMap = addUserPre(request.getUserIds(), createUser, path, module, projectId, project);
            request.getUserIds().forEach(userId -> {
                long count = QueryChain.of(UserRoleRelation.class)
                        .where(USER_ROLE_RELATION.USER_ID.eq(userId).and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId))
                                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue())))
                        .count();
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
                    LogDTO logDTO = new LogDTO(logProjectId, project.getOrganizationId(), adminRole.getId(), createUser, type, module, content + Translator.get("project_admin") + ": " + nameMap.get(userId));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logs);
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
        if (mapper.selectOneById(projectId) == null) {
            throw new CustomException(Translator.get("project_is_not_exist"));
        }
    }

    private void setLog(LogDTO operationLog, String path, String method, List<LogDTO> logs) {
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
                .and(SYSTEM_PROJECT.PROJECT_CODE.eq(systemProject.getProjectCode()))
                .and(SYSTEM_PROJECT.ID.ne(systemProject.getId()))).exists();
        if (exists) {
            throw new CustomException(Translator.get("project_name_already_exists"));
        }
    }
}
