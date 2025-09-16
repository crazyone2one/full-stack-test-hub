package cn.master.hub.service.impl;

import cn.master.hub.constants.InternalUserRole;
import cn.master.hub.constants.UserRoleType;
import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.project.ProjectUserRoleDTO;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.dto.request.project.ProjectUserRoleMemberEditRequest;
import cn.master.hub.dto.request.project.ProjectUserRoleMemberRequest;
import cn.master.hub.dto.request.project.ProjectUserRoleRequest;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRoleRelation;
import cn.master.hub.handler.PermissionCache;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.service.BaseUserRolePermissionService;
import cn.master.hub.service.BaseUserRoleRelationService;
import cn.master.hub.service.ProjectUserRoleService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.hub.entity.table.UserRoleTableDef.USER_ROLE;
import static cn.master.hub.handler.result.ResultCode.NO_PROJECT_USER_ROLE_PERMISSION;

/**
 * @author Created by 11's papa on 2025/9/15
 */
@Service
public class ProjectUserRoleServiceImpl extends BaseUserRoleServiceImpl implements ProjectUserRoleService {
    public ProjectUserRoleServiceImpl(BaseUserRolePermissionService baseUserRolePermissionService,
                                      BaseUserRoleRelationService baseUserRoleRelationService,
                                      PermissionCache permissionCache) {
        super(baseUserRolePermissionService, baseUserRoleRelationService, permissionCache);
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(String id) {
        UserRole userRole = mapper.selectOneById(id);
        checkProjectUserRole(userRole);
        return getPermissionSetting(userRole);
    }

    private void checkProjectUserRole(UserRole userRole) {
        if (!UserRoleType.PROJECT.name().equals(userRole.getType())) {
            throw new CustomException(NO_PROJECT_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public UserRole add(UserRole userRole) {
        userRole.setInternal(false);
        userRole.setType(UserRoleType.PROJECT.name());
        checkNewRoleExist(userRole);
        return super.add(userRole);
    }

    @Override
    public UserRole update(UserRole userRole) {
        UserRole oldRole = get(userRole.getId());
        // 非项目用户组, 全局用户组不允许修改
        checkProjectUserRole(oldRole);
        checkGlobalUserRole(oldRole);
        userRole.setType(UserRoleType.PROJECT.name());
        checkNewRoleExist(userRole);
        return super.update(userRole);
    }

    @Override
    public void delete(String roleId, String currentUserId) {
        UserRole userRole = get(roleId);
        // 非项目用户组不允许删除, 内置用户组不允许删除
        checkProjectUserRole(userRole);
        checkGlobalUserRole(userRole);
        super.delete(userRole, InternalUserRole.PROJECT_MEMBER.getValue(), currentUserId, userRole.getScopeId());
    }

    @Override
    public Page<SystemUser> listMember(ProjectUserRoleMemberRequest request) {
        return QueryChain.of(SystemUser.class)
                .select(SYSTEM_USER.ALL_COLUMNS).from(USER_ROLE_RELATION)
                .leftJoin(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.ROLE_ID.eq(request.getUserRoleId())
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getProjectId())))
                .and(SYSTEM_USER.NAME.like(request.getKeyword())
                        .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.like(request.getKeyword())))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc())
                .page(new Page<>(request.getPage(), request.getPageSize()));
    }

    @Override
    public void addMember(ProjectUserRoleMemberEditRequest request, String currentUserName) {
        SystemProject project = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ID.eq(request.getProjectId())).one();
        request.getUserIds().forEach(userId -> {
            checkMemberParam(userId, request.getUserRoleId());
            UserRoleRelation relation = new UserRoleRelation();
            relation.setUserId(userId);
            relation.setRoleId(request.getUserRoleId());
            relation.setSourceId(request.getProjectId());
            relation.setCreateUser(currentUserName);
            relation.setOrganizationId(project.getOrganizationId());
            baseUserRoleRelationService.save(relation);
        });
    }

    @Override
    public void removeMember(ProjectUserRoleMemberEditRequest request) {
        String removeUserId = request.getUserIds().getFirst();
        checkMemberParam(removeUserId, request.getUserRoleId());
        // 检查移除的是不是管理员
        if (Strings.CS.equals(request.getUserRoleId(), InternalUserRole.PROJECT_ADMIN.getValue())) {
            long count = QueryChain.of(UserRoleRelation.class)
                    .where(USER_ROLE_RELATION.USER_ID.ne(removeUserId)
                            .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getProjectId()))
                            .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))).count();
            if (count == 0) {
                throw new CustomException(Translator.get("keep_at_least_one_administrator"));
            }
        }
        // 移除项目-用户组的成员, 若成员只存在该项目下唯一用户组, 则提示不能移除
        long count = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.eq(removeUserId)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getProjectId()))
                        .and(USER_ROLE_RELATION.ROLE_ID.ne(InternalUserRole.PROJECT_ADMIN.getValue()))).count();
        if (count == 0) {
            throw new CustomException(Translator.get("project_at_least_one_user_role_require"));
        }
        QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.USER_ID.eq(removeUserId)
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getProjectId()))
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue())));
        baseUserRoleRelationService.remove(queryChain);
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        UserRole userRole = get(request.getUserRoleId());
        checkProjectUserRole(userRole);
        checkGlobalUserRole(userRole);
        super.updatePermissionSetting(request);
    }

    @Override
    public Page<ProjectUserRoleDTO> page(ProjectUserRoleRequest request) {
        Page<ProjectUserRoleDTO> page = queryChain()
                .where(USER_ROLE.TYPE.eq("PROJECT")
                        .and(USER_ROLE.SCOPE_ID.in("global", request.getProjectId())))
                .and(USER_ROLE.NAME.like(request.getKeyword()).or(USER_ROLE.ID.like(request.getKeyword())))
                .orderBy(USER_ROLE.INTERNAL.desc(), USER_ROLE.SCOPE_ID.desc(), USER_ROLE.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), ProjectUserRoleDTO.class);
        if (page.getRecords().isEmpty()) {
            return new Page<>();
        }
        List<String> roleIds = page.getRecords().stream().map(ProjectUserRoleDTO::getId).toList();
        List<UserRoleRelation> relations = getRelationByRoleIds(request.getProjectId(), roleIds);
        if (CollectionUtils.isNotEmpty(relations)) {
            Map<String, Long> countMap = relations.stream().collect(Collectors.groupingBy(UserRoleRelation::getRoleId, Collectors.counting()));
            page.getRecords().forEach(role -> {
                if (countMap.containsKey(role.getId())) {
                    role.setMemberCount(countMap.get(role.getId()).intValue());
                } else {
                    role.setMemberCount(0);
                }
            });
        } else {
            page.getRecords().forEach(role -> {
                role.setMemberCount(0);
            });
        }
        return page;
    }

    private List<UserRoleRelation> getRelationByRoleIds(String projectId, List<String> roleIds) {
        return QueryChain.of(UserRoleRelation.class).select(USER_ROLE_RELATION.ALL_COLUMNS).from(USER_ROLE_RELATION)
                .leftJoin(SYSTEM_USER).on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.ROLE_ID.in(roleIds).and(USER_ROLE_RELATION.SOURCE_ID.eq(projectId))).list();
    }
}
