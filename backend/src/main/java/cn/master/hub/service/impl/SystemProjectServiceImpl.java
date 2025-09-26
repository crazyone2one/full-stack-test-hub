package cn.master.hub.service.impl;

import cn.master.hub.constants.HttpMethodConstants;
import cn.master.hub.constants.InternalUserRole;
import cn.master.hub.constants.OperationLogConstants;
import cn.master.hub.dto.UserDTO;
import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.request.ProjectAddMemberRequest;
import cn.master.hub.dto.request.ProjectRequest;
import cn.master.hub.dto.request.ProjectSwitchRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.*;
import cn.master.hub.dto.system.request.ProjectMemberRequest;
import cn.master.hub.entity.*;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.invoker.ProjectServiceInvoker;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.handler.security.UserPrincipal;
import cn.master.hub.mapper.ProjectTestResourcePoolMapper;
import cn.master.hub.mapper.SystemProjectMapper;
import cn.master.hub.mapper.SystemUserMapper;
import cn.master.hub.mapper.UserRoleRelationMapper;
import cn.master.hub.service.AuthenticationUserService;
import cn.master.hub.service.CommonProjectService;
import cn.master.hub.service.OperationLogService;
import cn.master.hub.service.SystemProjectService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.ProjectTestResourcePoolTableDef.PROJECT_TEST_RESOURCE_POOL;
import static cn.master.hub.entity.table.SystemOrganizationTableDef.SYSTEM_ORGANIZATION;
import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.hub.entity.table.TestResourcePoolTableDef.TEST_RESOURCE_POOL;
import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.hub.entity.table.UserRoleTableDef.USER_ROLE;

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
    private final AuthenticationUserService authenticationUserService;
    private final CommonProjectService commonProjectService;
    private final static String PREFIX = "/system/project";
    private final static String ADD_PROJECT = PREFIX + "/add";
    private final static String UPDATE_PROJECT = PREFIX + "/update";
    private final static String REMOVE_PROJECT_MEMBER = PREFIX + "/remove-member/";
    private final static String ADD_MEMBER = PREFIX + "/add-member";

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
        buildUserInfo(page.getRecords());
        return page;
    }

    @Override
    public List<ProjectDTO> buildUserInfo(List<ProjectDTO> projectList) {
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
        return projectList;
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
                commonProjectService.setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
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

    @Override
    public ProjectDTO getProjectById(String id) {
        List<SystemProject> projectList = queryChain().where(SYSTEM_PROJECT.ID.eq(id).and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
        ProjectDTO projectDTO = new ProjectDTO();
        if (CollectionUtils.isNotEmpty(projectList)) {
            BeanUtils.copyProperties(projectList.getFirst(), projectDTO);
            projectDTO.setOrganizationName(QueryChain.of(SystemOrganization.class)
                    .where(SystemOrganization::getId).eq(projectDTO.getOrganizationId()).one().getName());
            List<ProjectDTO> projectDTOS = buildUserInfo(List.of(projectDTO));
            projectDTO = projectDTOS.getFirst();
        } else {
            return null;
        }
        return projectDTO;
    }

    @Override
    public ProjectDTO update(ProjectRequest request, String updateUser) {
        ProjectDTO projectDTO = new ProjectDTO();
        SystemProject project = new SystemProject();
        project.setId(request.getId());
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOrganizationId(request.getOrganizationId());
        project.setEnable(request.getEnable());
        project.setUpdateUser(updateUser);
        checkProjectExistByName(project);
        checkProjectNotExist(project.getId());
        projectDTO.setOrganizationName(QueryChain.of(SystemOrganization.class)
                .where(SystemOrganization::getId).eq(projectDTO.getOrganizationId()).one().getName());
        BeanUtils.copyProperties(project, projectDTO);
        mapper.update(project);
        return projectDTO;
    }

    @Override
    public UserDTO switchProject(ProjectSwitchRequest request, String currentUserId) {
        if (!Strings.CS.equals(currentUserId, request.getUserId())) {
            throw new CustomException(Translator.get("not_authorized"));
        }
        if (mapper.selectOneById(request.getProjectId()) == null) {
            throw new CustomException(Translator.get("project_not_exist"));
        }
        UpdateChain.of(SystemUser.class).set(SystemUser::getLastProjectId, request.getProjectId())
                .where(SystemUser::getId).eq(request.getUserId()).update();
        UserDTO userDTO = authenticationUserService.getUserDTO(request.getUserId());
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 创建新的 UserPrincipal 对象
        UserPrincipal updatedPrincipal = new UserPrincipal(userDTO);
        // 创建新的 Authentication 对象
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updatedPrincipal,
                authentication.getCredentials(),
                updatedPrincipal.getAuthorities()
        );
        // 更新安全上下文
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        return userDTO;
    }

    @Override
    public Page<ProjectDTO> getProjectPage(SystemProjectRequest request) {
        Page<ProjectDTO> page = QueryChain.of(SystemProject.class)
                .select(SYSTEM_PROJECT.ALL_COLUMNS, SYSTEM_ORGANIZATION.NAME.as("organizationName"))
                .from(SYSTEM_PROJECT).innerJoin(SYSTEM_ORGANIZATION).on(SYSTEM_PROJECT.ORGANIZATION_ID.eq(SYSTEM_ORGANIZATION.ID))
                .where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(request.getOrganizationId()))
                .and(SYSTEM_PROJECT.NUM.like(request.getKeyword()).or(SYSTEM_PROJECT.NAME.like(request.getKeyword())))
                .orderBy(SYSTEM_PROJECT.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), ProjectDTO.class);
        return buildUserInfo(page);
    }

    @Override
    public Page<UserExtendDTO> getProjectMember(ProjectMemberRequest request) {
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                .select(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.EMAIL, SYSTEM_USER.PHONE, USER_ROLE_RELATION.ROLE_ID, USER_ROLE_RELATION.CREATE_TIME.as("memberTime"))
                .from(USER_ROLE_RELATION).leftJoin(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getProjectId())
                        .and(SYSTEM_USER.NAME.like(request.getKeyword())
                                .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                                .or(SYSTEM_USER.PHONE.like(request.getKeyword()))))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc());
        Page<UserExtendDTO> page = queryChain()
                .select("temp.id,temp.name,temp.phone,temp.email, MAX( if (temp.role_id = 'project_admin', true, false)) as adminFlag, MIN(temp.memberTime) as groupTime")
                .from(userRoleRelationQueryChain).as("temp")
                .groupBy("temp.id").orderBy("adminFlag desc, groupTime desc")
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserExtendDTO.class);
        List<UserExtendDTO> memberList = page.getRecords();
        if (CollectionUtils.isNotEmpty(memberList)) {
            List<String> userIds = memberList.stream().map(UserExtendDTO::getId).toList();
            List<UserRoleOptionDTO> userRole = selectProjectUserRoleByUserIds(userIds, request.getProjectId());
            Map<String, List<UserRoleOptionDTO>> roleMap = userRole.stream().collect(Collectors.groupingBy(UserRoleOptionDTO::getUserId));
            memberList.forEach(user -> {
                if (roleMap.containsKey(user.getId())) {
                    user.setUserRoleList(roleMap.get(user.getId()));
                }
            });
        }
        return page;
    }

    @Override
    public void addMemberByProject(ProjectAddMemberRequest request, String currentUserName) {
        commonProjectService.addProjectUser(request, currentUserName, ADD_MEMBER,
                OperationLogType.ADD.name(), Translator.get("add"), OperationLogModule.SETTING_SYSTEM_ORGANIZATION);
    }

    @Override
    public int removeProjectMember(String projectId, String userId, String currentUserName) {
        return commonProjectService.removeProjectMember(projectId, userId, currentUserName, OperationLogModule.SETTING_SYSTEM_ORGANIZATION, StringUtils.join(REMOVE_PROJECT_MEMBER, projectId, "/", userId));
    }

    @Override
    public void addProjectMember(ProjectAddMemberBatchRequest request, String operator) {
        commonProjectService.addProjectMember(request, operator, ADD_MEMBER,
                OperationLogType.ADD.name(), Translator.get("add"), OperationLogModule.SETTING_SYSTEM_ORGANIZATION);
    }

    @Override
    public List<OrganizationProjectOptionsDTO> getProjectOptions(String organizationId) {
        return queryChain().where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId)).listAs(OrganizationProjectOptionsDTO.class);
    }

    private List<UserRoleOptionDTO> selectProjectUserRoleByUserIds(List<String> userIds, String projectId) {
        return QueryChain.of(UserRoleRelation.class)
                .select(QueryMethods.distinct(USER_ROLE_RELATION.ROLE_ID.as("id"), USER_ROLE.NAME, USER_ROLE_RELATION.USER_ID))
                .from(USER_ROLE_RELATION).leftJoin(USER_ROLE).on(USER_ROLE_RELATION.ROLE_ID.eq(USER_ROLE.ID))
                .where(USER_ROLE_RELATION.USER_ID.in(userIds).and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId)))
                .listAs(UserRoleOptionDTO.class);
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
            Map<String, String> nameMap = commonProjectService.addUserPre(request.getUserIds(), createUser, path, module, projectId, project);
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
                    commonProjectService.setLog(logDTO, path, HttpMethodConstants.POST.name(), logs);
                }
            });
        });
        if (!userRoleRelations.isEmpty()) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
        operationLogService.batchAdd(logs);
    }

    private void checkProjectNotExist(String projectId) {
        if (mapper.selectOneById(projectId) == null) {
            throw new CustomException(Translator.get("project_is_not_exist"));
        }
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
