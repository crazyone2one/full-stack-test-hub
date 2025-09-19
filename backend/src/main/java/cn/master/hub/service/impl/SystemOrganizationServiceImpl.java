package cn.master.hub.service.impl;

import cn.master.hub.constants.*;
import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.system.*;
import cn.master.hub.dto.system.request.OrgMemberExtendProjectRequest;
import cn.master.hub.dto.system.request.OrganizationMemberExtendRequest;
import cn.master.hub.dto.system.request.OrganizationMemberRequest;
import cn.master.hub.dto.system.request.OrganizationMemberUpdateRequest;
import cn.master.hub.entity.*;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.log.LogDTO;
import cn.master.hub.handler.log.OperationLogModule;
import cn.master.hub.handler.log.OperationLogType;
import cn.master.hub.mapper.SystemOrganizationMapper;
import cn.master.hub.mapper.UserRoleRelationMapper;
import cn.master.hub.service.OperationLogService;
import cn.master.hub.service.SystemOrganizationService;
import cn.master.hub.util.JacksonUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.hub.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 组织 服务层实现。
 *
 * @author the2n
 * @since 2025-09-01
 */
@Service
@RequiredArgsConstructor
public class SystemOrganizationServiceImpl extends ServiceImpl<SystemOrganizationMapper, SystemOrganization> implements SystemOrganizationService {
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final OperationLogService operationLogService;
    private static final String ADD_MEMBER_PATH = "/system/organization/add-member";
    private static final String REMOVE_MEMBER_PATH = "/system/organization/remove-member";
    public static final Integer DEFAULT_REMAIN_DAY_COUNT = 30;
    private static final Long DEFAULT_ORGANIZATION_NUM = 100001L;

    @Override
    public List<OptionDTO> listAll() {
        List<SystemOrganization> organizations = mapper.selectAll();
        return organizations.stream().map(o -> new OptionDTO(o.getId(), o.getName())).toList();
    }

    @Override
    public Map<String, Long> getTotal(String organizationId) {
        Map<String, Long> total = new HashMap<>();
        if (StringUtils.isBlank(organizationId)) {
            // 统计所有项目
            total.put("projectTotal", QueryChain.of(SystemProject.class).count());
            total.put("organizationTotal", queryChain().count());
        } else {
            total.put("projectTotal", QueryChain.of(SystemProject.class).where(SystemProject::getOrganizationId).eq(organizationId).count());
            total.put("organizationTotal", 1L);
        }
        return total;
    }

    @Override
    public void update(OrganizationDTO organizationDTO) {
        checkOrganizationNotExist(organizationDTO.getId());
        checkOrganizationExist(organizationDTO);
        mapper.update(organizationDTO);
        // 新增的组织管理员ID
        List<String> addOrgAdmins = organizationDTO.getUserIds();
        // 旧的组织管理员ID
        List<String> oldOrgAdmins = getOrgAdminIds(organizationDTO.getId());
        // 需要新增组织管理员ID
        List<String> addIds = addOrgAdmins.stream().filter(addOrgAdmin -> !oldOrgAdmins.contains(addOrgAdmin)).toList();
        // 需要删除的组织管理员ID
        List<String> deleteIds = oldOrgAdmins.stream().filter(oldOrgAdmin -> !addOrgAdmins.contains(oldOrgAdmin)).toList();
        // 添加组织管理员
        if (CollectionUtils.isNotEmpty(addIds)) {
            addIds.forEach(userId -> {
                // 添加组织管理员
                createAdmin(userId, organizationDTO.getId(), organizationDTO.getUpdateUser());
            });
        }
        // 删除组织管理员
        if (CollectionUtils.isNotEmpty(deleteIds)) {
            QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.in(deleteIds)
                    .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ORG_ADMIN.getValue()))
                    .and(USER_ROLE_RELATION.SOURCE_ID.eq(organizationDTO.getId())));
            userRoleRelationMapper.deleteByQuery(queryChain);
        }
    }

    @Override
    public Page<UserExtendDTO> getMemberPageBySystem(OrganizationProjectRequest request) {
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                .select(SYSTEM_USER.ALL_COLUMNS, USER_ROLE_RELATION.ROLE_ID, USER_ROLE_RELATION.CREATE_TIME.as("memberTime"))
                .from(USER_ROLE_RELATION).join(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId()))
                .and(SYSTEM_USER.NAME.like(request.getKeyword())
                        .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.like(request.getKeyword())))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc());
        Page<UserExtendDTO> userExtendDTOS = queryChain().select("temp.*")
                .select("max(if(temp.role_id = 'org_admin', true, false)) as adminFlag")
                .select("min(temp.memberTime) as groupTime").from(userRoleRelationQueryChain.as("temp"))
                .groupBy("temp.id")
                .orderBy("adminFlag desc, groupTime desc").pageAs(new Page<>(request.getPage(), request.getPageSize()), UserExtendDTO.class);
        if (CollectionUtils.isNotEmpty(userExtendDTOS.getRecords())) {
            List<String> userIds = userExtendDTOS.getRecords().stream().map(UserExtendDTO::getId).toList();
            List<UserRoleOptionDTO> userRole = selectUserRoleByUserIds(userIds, request.getOrganizationId());
            Map<String, List<UserRoleOptionDTO>> roleMap = userRole.stream().collect(Collectors.groupingBy(UserRoleOptionDTO::getUserId));
            userExtendDTOS.getRecords().forEach(user -> {
                if (roleMap.containsKey(user.getId())) {
                    user.setUserRoleList(roleMap.get(user.getId()));
                }
            });

        }
        return userExtendDTOS;
    }

    @Override
    public Page<OrgUserExtend> getMemberPageByOrg(OrganizationProjectRequest request) {
        String organizationId = request.getOrganizationId();
        Page<OrgUserExtend> page = genQueryChain(request).pageAs(new Page<>(request.getPage(), request.getPageSize()), OrgUserExtend.class);
        List<OrgUserExtend> orgUserExtends = page.getRecords();
        Map<String, OrgUserExtend> userMap = orgUserExtends.stream().collect(Collectors.toMap(OrgUserExtend::getId, user -> user));
        List<UserRoleRelation> userRoleRelationsByUsers = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.in(orgUserExtends.stream().map(OrgUserExtend::getId).toList()))
                .and(USER_ROLE_RELATION.ORGANIZATION_ID.eq(organizationId)).orderBy(USER_ROLE_RELATION.CREATE_TIME.desc()).list();
        //根据关系表查询出用户的关联组织和用户组
        Map<String, Set<String>> userIdRoleIdMap = new HashMap<>();
        Map<String, Set<String>> userIdProjectIdMap = new HashMap<>();
        Set<String> roleIdSet = new HashSet<>();
        Set<String> projectIdSet = new HashSet<>();
        for (UserRoleRelation userRoleRelationsByUser : userRoleRelationsByUsers) {
            String sourceId = userRoleRelationsByUser.getSourceId();
            String roleId = userRoleRelationsByUser.getRoleId();
            String userId = userRoleRelationsByUser.getUserId();
            //收集组织级别的用户组
            if (Strings.CS.equals(sourceId, organizationId)) {
                getTargetIds(userIdRoleIdMap, roleIdSet, roleId, userId);
            }
            //收集项目id
            if (!Strings.CS.equals(sourceId, organizationId)) {
                getTargetIds(userIdProjectIdMap, projectIdSet, sourceId, userId);
            }
        }
        List<UserRole> userRoles = QueryChain.of(UserRole.class).where(USER_ROLE.ID.in(roleIdSet)).list();
        List<SystemProject> projects = new ArrayList<>();
        if (!projectIdSet.isEmpty()) {
            projects = QueryChain.of(SystemProject.class).where(SystemProject::getId).in(projectIdSet).list();
        }
        for (OrgUserExtend orgUserExtend : orgUserExtends) {
            if (!projects.isEmpty()) {
                Set<String> projectIds = userIdProjectIdMap.get(orgUserExtend.getId());
                if (CollectionUtils.isNotEmpty(projectIds)) {
                    List<SystemProject> projectFilters = projects.stream().filter(t -> projectIds.contains(t.getId())).toList();
                    List<OptionDTO> projectList = new ArrayList<>();
                    setProjectList(projectList, projectFilters);
                    orgUserExtend.setProjectIdNameMap(projectList);
                }
            }

            Set<String> userRoleIds = userIdRoleIdMap.get(orgUserExtend.getId());
            List<UserRole> userRoleFilters = userRoles.stream().filter(t -> userRoleIds.contains(t.getId())).toList();
            List<OptionDTO> userRoleList = new ArrayList<>();
            setUserRoleList(userRoleList, userRoleFilters);
            orgUserExtend.setUserRoleIdNameMap(userRoleList);

        }
        return page;
    }

    private QueryChain<SystemOrganization> genQueryChain(OrganizationProjectRequest request) {
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                .select(SYSTEM_USER.ALL_COLUMNS, USER_ROLE_RELATION.ROLE_ID, USER_ROLE_RELATION.CREATE_TIME.as("memberTime"))
                .from(USER_ROLE_RELATION).join(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId()))
                .and(SYSTEM_USER.NAME.like(request.getKeyword())
                        .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.like(request.getKeyword())))
                .orderBy(SYSTEM_USER.UPDATE_TIME.desc());
        return queryChain().select("temp.*")
                .select("min(temp.memberTime) as groupTime")
                .from(userRoleRelationQueryChain.as("temp"))
                .groupBy("temp.id")
                .orderBy("adminFlag desc, groupTime desc");
    }

    private void setUserRoleList(List<OptionDTO> userRoleList, List<UserRole> userRoleFilters) {
        for (UserRole userRole : userRoleFilters) {
            OptionDTO optionDTO = new OptionDTO();
            optionDTO.setValue(userRole.getId());
            optionDTO.setLabel(userRole.getName());
            userRoleList.add(optionDTO);
        }
    }

    private void setProjectList(List<OptionDTO> projectList, List<SystemProject> projectFilters) {
        for (SystemProject project : projectFilters) {
            OptionDTO optionDTO = new OptionDTO();
            optionDTO.setValue(project.getId());
            optionDTO.setLabel(project.getName());
            projectList.add(optionDTO);
        }
    }

    private void getTargetIds(Map<String, Set<String>> userIdTargetIdMap, Set<String> targetIdSet, String sourceId, String userId) {
        Set<String> targetIds = userIdTargetIdMap.get(userId);
        if (CollectionUtils.isEmpty(targetIds)) {
            targetIds = new HashSet<>();
        }
        targetIds.add(sourceId);
        targetIdSet.add(sourceId);
        userIdTargetIdMap.put(userId, targetIds);
    }

    @Override
    public void addMemberBySystem(OrganizationMemberRequest request, String currentUserName) {
        List<LogDTO> logs = new ArrayList<>();
        addMemberAndGroup(request, currentUserName);
        List<SystemUser> users = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.in(request.getUserIds())).list();
        List<String> nameList = users.stream().map(SystemUser::getName).collect(Collectors.toList());
        setLog(request.getOrganizationId(), currentUserName, OperationLogType.ADD.name(), Translator.get("add") + Translator.get("organization_member_log") + ": " + StringUtils.join(nameList, ","), ADD_MEMBER_PATH, null, null, logs);
        operationLogService.batchAdd(logs);
    }

    @Override
    public void addMemberByOrg(OrganizationMemberExtendRequest organizationMemberExtendRequest, String createUserId) {
        String organizationId = organizationMemberExtendRequest.getOrganizationId();
        checkOrgExistById(organizationId);
        Map<String, SystemUser> userMap;
        userMap = getUserMap(organizationMemberExtendRequest);
        Map<String, UserRole> userRoleMap = checkUseRoleExist(organizationMemberExtendRequest.getUserRoleIds(), organizationId);
        setRelationByMemberAndGroupIds(organizationMemberExtendRequest, createUserId, userMap, userRoleMap, true);
    }

    private void setRelationByMemberAndGroupIds(OrganizationMemberExtendRequest organizationMemberExtendRequest, String createUserId, Map<String, SystemUser> userMap, Map<String, UserRole> userRoleMap, boolean add) {
        List<LogDTO> logDTOList = new ArrayList<>();
        String organizationId = organizationMemberExtendRequest.getOrganizationId();
        userMap.keySet().forEach(memberId -> {
            if (userMap.get(memberId) == null) {
                throw new CustomException("id:" + memberId + Translator.get("user.not.exist"));
            }
            organizationMemberExtendRequest.getUserRoleIds().forEach(userRoleId -> {
                if (userRoleMap.get(userRoleId) != null) {
                    //过滤已存在的关系
                    QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class)
                            .where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)
                                    .and(USER_ROLE_RELATION.USER_ID.eq(memberId))
                                    .and(USER_ROLE_RELATION.ROLE_ID.eq(userRoleId)));
                    List<UserRoleRelation> userRoleRelations = userRoleRelationMapper.selectListByQuery(queryChain);
                    if (CollectionUtils.isEmpty(userRoleRelations)) {
                        UserRoleRelation userRoleRelation = buildUserRoleRelation(createUserId, memberId, organizationId, userRoleId, organizationId);
                        userRoleRelation.setOrganizationId(organizationId);
                        userRoleRelationMapper.insert(userRoleRelation);
                        //add Log
                        String path = add ? "/organization/add-member" : "/organization/role/update-member";
                        String type = add ? OperationLogType.ADD.name() : OperationLogType.UPDATE.name();
                        LogDTO dto = new LogDTO(
                                OperationLogConstants.ORGANIZATION,
                                organizationId,
                                memberId,
                                createUserId,
                                type,
                                OperationLogModule.SETTING_ORGANIZATION_MEMBER,
                                userMap.get(memberId).getName());
                        setLog(dto, path, logDTOList, userRoleRelation);
                    }
                }
            });
        });
        operationLogService.batchAdd(logDTOList);
    }

    private static void setLog(LogDTO dto, String path, List<LogDTO> logDTOList, Object originalValue) {
        dto.setPath(path);
        dto.setMethod(HttpMethodConstants.POST.name());
        dto.setOriginalValue(JacksonUtils.toJSONBytes(originalValue));
        logDTOList.add(dto);
    }

    private UserRoleRelation buildUserRoleRelation(String createUserId, String memberId, String sourceId, String roleId, String organizationId) {
        UserRoleRelation userRoleRelation = new UserRoleRelation();
        userRoleRelation.setUserId(memberId);
        userRoleRelation.setOrganizationId(organizationId);
        userRoleRelation.setSourceId(sourceId);
        userRoleRelation.setRoleId(roleId);
        userRoleRelation.setCreateUser(createUserId);
        return userRoleRelation;
    }

    private Map<String, UserRole> checkUseRoleExist(List<String> userRoleIds, String organizationId) {
        List<UserRole> userRoles = QueryChain.of(UserRole.class)
                .where(USER_ROLE.ID.in(userRoleIds)
                        .and(USER_ROLE.SCOPE_ID.in(Arrays.asList(UserRoleEnum.GLOBAL.toString(), organizationId)))
                        .and(USER_ROLE.TYPE.eq(UserRoleType.ORGANIZATION.toString())))
                .list();

        if (CollectionUtils.isEmpty(userRoles)) {
            throw new CustomException(Translator.get("user_role_not_exist"));
        }
        return userRoles.stream().collect(Collectors.toMap(UserRole::getId, user -> user));
    }

    private Map<String, SystemUser> getUserMap(OrganizationMemberExtendRequest organizationMemberExtendRequest) {
        Map<String, SystemUser> userMap;
        if (organizationMemberExtendRequest.isSelectAll()) {
            OrganizationProjectRequest organizationRequest = new OrganizationProjectRequest();
            BeanUtils.copyProperties(organizationMemberExtendRequest, organizationRequest);
            List<OrgUserExtend> orgUserExtends = genQueryChain(organizationRequest).listAs(OrgUserExtend.class);
            List<String> excludeIds = organizationMemberExtendRequest.getExcludeIds();
            if (CollectionUtils.isNotEmpty(excludeIds)) {
                userMap = orgUserExtends.stream().filter(user -> !excludeIds.contains(user.getId())).collect(Collectors.toMap(SystemUser::getId, user -> user));
            } else {
                userMap = orgUserExtends.stream().collect(Collectors.toMap(SystemUser::getId, user -> user));
            }
        } else {
            userMap = checkUserExist(organizationMemberExtendRequest.getMemberIds());
        }
        return userMap;
    }

    @Override
    public void removeMember(String organizationId, String userId, String currentUserName) {
        List<LogDTO> logs = new ArrayList<>();
        checkOrgExistById(organizationId);
        //检查用户是不是最后一个管理员
        QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ORG_ADMIN.getValue())));
        if (userRoleRelationMapper.selectCountByQuery(queryChain) == 0) {
            throw new CustomException(Translator.get("keep_at_least_one_administrator"));
        }
        //删除组织下项目与成员的关系
        List<String> projectIds = getProjectIds(organizationId);
        if (CollectionUtils.isNotEmpty(projectIds)) {
            QueryChain<UserRoleRelation> chain = QueryChain.of(UserRoleRelation.class)
                    .where(USER_ROLE_RELATION.USER_ID.eq(userId)
                            .and(USER_ROLE_RELATION.SOURCE_ID.in(projectIds)));
            userRoleRelationMapper.deleteByQuery(chain);
        }
        //删除组织与成员的关系
        QueryChain<UserRoleRelation> chain = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.eq(userId)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)));
        userRoleRelationMapper.deleteByQuery(chain);
        // 操作记录
        SystemUser user = QueryChain.of(SystemUser.class).where(SystemUser::getId).eq(userId).one();
        setLog(organizationId, currentUserName, OperationLogType.DELETE.name(), Translator.get("delete") + Translator.get("organization_member_log") + ": " + user.getName(), REMOVE_MEMBER_PATH, user, null, logs);
        operationLogService.batchAdd(logs);
    }

    @Override
    public void addMemberRole(OrganizationMemberExtendRequest request, String currentUserName) {
        String organizationId = request.getOrganizationId();
        checkOrgExistById(organizationId);
        Map<String, SystemUser> userMap;
        userMap = getUserMap(request);
        Map<String, UserRole> userRoleMap = checkUseRoleExist(request.getUserRoleIds(), organizationId);
        //在新增组织成员与用户组和组织的关系
        setRelationByMemberAndGroupIds(request, currentUserName, userMap, userRoleMap, false);
    }

    @Override
    public void updateMember(OrganizationMemberUpdateRequest organizationMemberUpdateRequest, String currentUserName, String path, String module) {
        String organizationId = organizationMemberUpdateRequest.getOrganizationId();
        //校验组织是否存在
        checkOrgExistById(organizationId);
        //校验用户是否存在
        String memberId = organizationMemberUpdateRequest.getMemberId();
        SystemUser user = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.eq(memberId)).one();
        if (user == null) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
        //校验成员是否是当前组织的成员
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(memberId)
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)));
        List<UserRoleRelation> userRoleRelations = userRoleRelationMapper.selectListByQuery(userRoleRelationQueryChain);
        if (CollectionUtils.isEmpty(userRoleRelations)) {
            throw new CustomException(Translator.get("organization_member_not_exist"));
        }

        List<LogDTO> logDTOList = new ArrayList<>();
        //更新用户组
        List<String> userRoleIds = organizationMemberUpdateRequest.getUserRoleIds();
        updateUserRoleRelation(currentUserName, organizationId, user, userRoleIds, logDTOList, path, module);
        //更新项目
        List<String> projectIds = organizationMemberUpdateRequest.getProjectIds();
        if (CollectionUtils.isNotEmpty(projectIds)) {
            updateProjectUserRelation(currentUserName, organizationId, user, projectIds, logDTOList);
        } else {
            List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId)).list();
            if (CollectionUtils.isNotEmpty(projects)) {
                List<String> projectInDBInOrgIds = projects.stream().map(SystemProject::getId).collect(Collectors.toList());
                QueryChain<UserRoleRelation> userRoleRelationExample = QueryChain.of(UserRoleRelation.class)
                        .where(USER_ROLE_RELATION.USER_ID.eq(memberId).and(USER_ROLE_RELATION.SOURCE_ID.in(projectInDBInOrgIds)));
                userRoleRelationMapper.deleteByQuery(userRoleRelationExample);
            }
        }
        //写入操作日志
        operationLogService.batchAdd(logDTOList);
    }

    @Override
    public void addMemberToProject(OrgMemberExtendProjectRequest orgMemberExtendProjectRequest, String currentUserName) {
        String requestOrganizationId = orgMemberExtendProjectRequest.getOrganizationId();
        checkOrgExistById(requestOrganizationId);
        List<LogDTO> logDTOList = new ArrayList<>();
        List<String> projectIds = orgMemberExtendProjectRequest.getProjectIds();
        //用户不在当前组织内过掉
        Map<String, SystemUser> userMap;
        if (orgMemberExtendProjectRequest.isSelectAll()) {
            OrganizationProjectRequest organizationRequest = new OrganizationProjectRequest();
            BeanUtils.copyProperties(orgMemberExtendProjectRequest, organizationRequest);
            List<OrgUserExtend> orgUserExtends = genQueryChain(organizationRequest).listAs(OrgUserExtend.class);
            List<String> excludeIds = orgMemberExtendProjectRequest.getExcludeIds();
            if (CollectionUtils.isNotEmpty(excludeIds)) {
                userMap = orgUserExtends.stream().filter(user -> !excludeIds.contains(user.getId())).collect(Collectors.toMap(SystemUser::getId, user -> user));
            } else {
                userMap = orgUserExtends.stream().collect(Collectors.toMap(SystemUser::getId, user -> user));
            }
        } else {
            userMap = checkUserExist(orgMemberExtendProjectRequest.getMemberIds());
        }
        List<String> userIds = userMap.values().stream().map(SystemUser::getId).toList();
        userIds.forEach(memberId -> {
            projectIds.forEach(projectId -> {
                //过滤已存在的关系
                QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                        .where(USER_ROLE_RELATION.USER_ID.eq(memberId)
                                .and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId)));
                List<UserRoleRelation> userRoleRelations = userRoleRelationMapper.selectListByQuery(userRoleRelationQueryChain);
                if (CollectionUtils.isEmpty(userRoleRelations)) {
                    UserRoleRelation userRoleRelation = buildUserRoleRelation(currentUserName, memberId, projectId, InternalUserRole.PROJECT_MEMBER.getValue(), requestOrganizationId);
                    userRoleRelation.setOrganizationId(orgMemberExtendProjectRequest.getOrganizationId());
                    userRoleRelationMapper.insert(userRoleRelation);
                    //add Log
                    LogDTO dto = new LogDTO(
                            projectId,
                            requestOrganizationId,
                            memberId,
                            currentUserName,
                            OperationLogType.ADD.name(),
                            OperationLogModule.PROJECT_MANAGEMENT_PERMISSION_MEMBER,
                            "");
                    setLog(dto, "/organization/project/add-member", logDTOList, userRoleRelation);
                }
            });
        });
        //写入操作日志
        operationLogService.batchAdd(logDTOList);
    }

    @Override
    public List<OptionDTO> getUserRoleList(String organizationId) {
        //校验组织是否存在
        checkOrgExistById(organizationId);
        List<String> scopeIds = Arrays.asList(UserRoleEnum.GLOBAL.toString(), organizationId);
        List<OptionDTO> userRoleList = new ArrayList<>();
        List<UserRole> userRoles = QueryChain.of(UserRole.class)
                .where(USER_ROLE.TYPE.eq(UserRoleType.ORGANIZATION.toString()).and(USER_ROLE.SCOPE_ID.in(scopeIds)))
                .list();
        setUserRoleList(userRoleList, userRoles);
        return userRoleList;
    }

    private void updateProjectUserRelation(String currentUserName, String organizationId, SystemUser user, List<String> projectIds, List<LogDTO> logDTOList) {
        Map<String, SystemProject> projectMap = checkProjectExist(projectIds, organizationId);
        List<String> projectInDBInOrgIds = projectMap.values().stream().map(SystemProject::getId).toList();
        //删除旧的关系
        String memberId = user.getId();
        List<SystemProject> projects = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId)).list();
        List<String> projectIdsAll = projects.stream().map(SystemProject::getId).toList();
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.eq(memberId).and(USER_ROLE_RELATION.SOURCE_ID.in(projectIdsAll)));
        userRoleRelationMapper.deleteByQuery(userRoleRelationQueryChain);
        projectInDBInOrgIds.forEach(projectId -> {
            UserRoleRelation userRoleRelation = buildUserRoleRelation(currentUserName, memberId, projectId, InternalUserRole.PROJECT_MEMBER.getValue(), organizationId);
            userRoleRelation.setOrganizationId(organizationId);
            userRoleRelationMapper.insert(userRoleRelation);
        });
    }

    private Map<String, SystemProject> checkProjectExist(List<String> projectIds, String organizationId) {
        List<SystemProject> projects = QueryChain.of(SystemProject.class)
                .where(SYSTEM_PROJECT.ID.in(projectIds).and(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId))).list();
        if (CollectionUtils.isEmpty(projects)) {
            throw new CustomException(Translator.get("project_not_exist"));
        }
        return projects.stream().collect(Collectors.toMap(SystemProject::getId, project -> project));
    }

    private void updateUserRoleRelation(String currentUserName, String organizationId, SystemUser user, List<String> userRoleIds, List<LogDTO> logDTOList, String path, String module) {
        //检查用户组是否是组织级别用户组
        String memberId = user.getId();
        Map<String, UserRole> userRoleMap = checkUseRoleExist(userRoleIds, organizationId);
        List<String> userRoleInDBInOrgIds = userRoleMap.values().stream().map(UserRole::getId).toList();
        //删除旧的关系
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.eq(memberId)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)));
        userRoleRelationMapper.deleteByQuery(userRoleRelationQueryChain);
        userRoleInDBInOrgIds.forEach(userRoleId -> {
            UserRoleRelation userRoleRelation = buildUserRoleRelation(currentUserName, memberId, organizationId, userRoleId, organizationId);
            userRoleRelation.setOrganizationId(organizationId);
            userRoleRelationMapper.insert(userRoleRelation);
        });
        //add Log
        LogDTO dto = new LogDTO(
                OperationLogConstants.ORGANIZATION,
                organizationId,
                memberId,
                currentUserName,
                OperationLogType.UPDATE.name(),
                module,
                user.getName());
        setLog(dto, path, logDTOList, userRoleInDBInOrgIds);
    }

    private List<String> getProjectIds(String organizationId) {
        return QueryChain.of(SystemProject.class)
                .where(SystemProject::getOrganizationId).eq(organizationId)
                .list().stream().map(SystemProject::getId).toList();
    }

    private void checkOrgExistById(String organizationId) {
        if (Objects.isNull(mapper.selectOneById(organizationId))) {
            throw new CustomException(Translator.get("organization_not_exist"));
        }
    }

    private void addMemberAndGroup(OrganizationMemberRequest request, String currentUserName) {
        checkOrgExistByIds(List.of(request.getOrganizationId()));
        Map<String, SystemUser> userMap = checkUserExist(request.getUserIds());
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        for (String userId : request.getUserIds()) {
            if (userMap.get(userId) == null) {
                throw new CustomException(Translator.get("user.not.exist") + ", id: " + userId);
            }
            boolean exists = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.eq(userId)
                    .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId()))).exists();
            if (exists) {
                continue;
            }
            request.getUserRoleIds().forEach(userRoleId -> {
                UserRoleRelation userRoleRelation = new UserRoleRelation();

                userRoleRelation.setUserId(userId);
                userRoleRelation.setSourceId(request.getOrganizationId());
                userRoleRelation.setRoleId(userRoleId);

                userRoleRelation.setCreateUser(currentUserName);
                userRoleRelation.setOrganizationId(request.getOrganizationId());
                userRoleRelations.add(userRoleRelation);
            });
        }
        if (CollectionUtils.isNotEmpty(userRoleRelations)) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
    }

    private Map<String, SystemUser> checkUserExist(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new CustomException(Translator.get("user.not.empty"));
        }
        List<SystemUser> users = QueryChain.of(SystemUser.class).where(SystemUser::getId).in(userIds).list();
        if (CollectionUtils.isEmpty(users)) {
            throw new CustomException(Translator.get("user.not.exist"));
        }
        return users.stream().collect(Collectors.toMap(SystemUser::getId, user -> user));
    }

    private void checkOrgExistByIds(List<String> organizationId) {
        long count = queryChain().where(SystemOrganization::getId).in(organizationId).count();
        if (count < organizationId.size()) {
            throw new CustomException(Translator.get("organization_not_exist"));
        }
    }

    private void setLog(String organizationId, String createUser, String type, String content, String path, Object originalValue, Object modifiedValue, List<LogDTO> logs) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                organizationId,
                createUser,
                type,
                OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                content);
        dto.setPath(path);
        dto.setMethod(HttpMethodConstants.POST.name());
        dto.setOriginalValue(JacksonUtils.toJSONBytes(originalValue));
        dto.setModifiedValue(JacksonUtils.toJSONBytes(modifiedValue));
        logs.add(dto);
    }

    private List<UserRoleOptionDTO> selectUserRoleByUserIds(List<String> userIds, String organizationId) {
        return QueryChain.of(UserRoleRelation.class)
                .select(QueryMethods.distinct(USER_ROLE_RELATION.ROLE_ID, USER_ROLE_RELATION.USER_ID, USER_ROLE.NAME))
                .from(USER_ROLE_RELATION)
                .leftJoin(USER_ROLE).on(USER_ROLE.ID.eq(USER_ROLE_RELATION.ROLE_ID))
                .where(USER_ROLE_RELATION.USER_ID.in(userIds).and(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId))
                        .and(USER_ROLE_RELATION.ORGANIZATION_ID.eq(organizationId))).listAs(UserRoleOptionDTO.class);
    }

    private void createAdmin(String memberId, String organizationId, String createUser) {
        UserRoleRelation orgAdmin = new UserRoleRelation();
        orgAdmin.setUserId(memberId);
        orgAdmin.setRoleId(InternalUserRole.ORG_ADMIN.getValue());
        orgAdmin.setSourceId(organizationId);
        orgAdmin.setCreateUser(createUser);
        orgAdmin.setOrganizationId(organizationId);
        userRoleRelationMapper.insertSelective(orgAdmin);
    }

    private List<String> getOrgAdminIds(String organizationId) {
        return QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId)
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ORG_ADMIN.getValue())))
                .list().stream().map(UserRoleRelation::getUserId).toList();
    }

    private void checkOrganizationExist(OrganizationDTO organizationDTO) {
        SystemOrganization one = queryChain().where(SystemOrganization::getName).eq(organizationDTO.getName())
                .and(SystemOrganization::getId).ne(organizationDTO.getId()).one();
        if (one != null) {
            throw new CustomException(Translator.get("organization_name_already_exists"));
        }
    }

    private void checkOrganizationNotExist(String id) {
        if (mapper.selectOneById(id) == null) {
            throw new CustomException(Translator.get("organization_not_exist"));
        }
    }
}
