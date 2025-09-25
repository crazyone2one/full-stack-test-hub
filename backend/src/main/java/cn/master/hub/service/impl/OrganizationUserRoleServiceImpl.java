package cn.master.hub.service.impl;

import cn.master.hub.constants.InternalUserRole;
import cn.master.hub.constants.UserRoleEnum;
import cn.master.hub.constants.UserRoleType;
import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.dto.system.request.OrganizationUserRoleMemberEditRequest;
import cn.master.hub.dto.system.request.OrganizationUserRoleMemberRequest;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRoleRelation;
import cn.master.hub.handler.PermissionCache;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.service.BaseUserRolePermissionService;
import cn.master.hub.service.BaseUserRoleRelationService;
import cn.master.hub.service.OrganizationUserRoleService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.hub.handler.result.ResultCode.NO_ORG_USER_ROLE_PERMISSION;

/**
 * @author Created by 11's papa on 2025/9/15
 */
@Service
public class OrganizationUserRoleServiceImpl extends BaseUserRoleServiceImpl implements OrganizationUserRoleService {
    public OrganizationUserRoleServiceImpl(BaseUserRolePermissionService baseUserRolePermissionService,
                                           BaseUserRoleRelationService baseUserRoleRelationService,
                                           PermissionCache permissionCache) {
        super(baseUserRolePermissionService, baseUserRoleRelationService, permissionCache);
    }

    @Override
    public List<UserRole> list(String organizationId) {
        List<UserRole> userRoles = queryChain()
                .where(UserRole::getType).eq(UserRoleType.ORGANIZATION.name())
                .and(UserRole::getScopeId).in(Arrays.asList(organizationId, UserRoleEnum.GLOBAL.toString()))
                .orderBy(UserRole::getCreateTime).desc()
                .list();
        userRoles.sort(Comparator.comparing(UserRole::getInternal).thenComparing(UserRole::getScopeId)
                .thenComparing(Comparator.comparing(UserRole::getCreateTime).thenComparing(UserRole::getId).reversed()).reversed());
        return userRoles;

    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(String id) {
        UserRole userRole = get(id);
        checkOrgUserRole(userRole);
        return getPermissionSetting(userRole);
    }

    private void checkOrgUserRole(UserRole userRole) {
        if (!UserRoleType.ORGANIZATION.name().equals(userRole.getType())) {
            throw new CustomException(NO_ORG_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        UserRole userRole = get(request.getUserRoleId());
        checkOrgUserRole(userRole);
        checkGlobalUserRole(userRole);
        super.updatePermissionSetting(request);
    }

    @Override
    public Page<SystemUser> listMember(OrganizationUserRoleMemberRequest request) {
        return QueryChain.of(SystemUser.class)
                .select(SYSTEM_USER.ALL_COLUMNS).from(USER_ROLE_RELATION).leftJoin(SYSTEM_USER)
                .on(USER_ROLE_RELATION.USER_ID.eq(SYSTEM_USER.ID))
                .where(USER_ROLE_RELATION.ROLE_ID.eq(request.getUserRoleId())
                        .and(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())))
                .and(SYSTEM_USER.NAME.like(request.getKeyword())
                        .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.like(request.getKeyword())))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc())
                .page(new Page<>(request.getPage(), request.getPageSize()));
    }

    @Override
    public void addMember(OrganizationUserRoleMemberEditRequest request, String operator) {
        request.getUserIds().forEach(userId -> {
            checkMemberParam(userId, request.getUserRoleId());
            UserRoleRelation relation = new UserRoleRelation();
            relation.setUserId(userId);
            relation.setRoleId(request.getUserRoleId());
            relation.setSourceId(request.getOrganizationId());
            relation.setCreateUser(operator);
            relation.setOrganizationId(request.getOrganizationId());
            baseUserRoleRelationService.save(relation);
        });
    }

    @Override
    public void removeMember(OrganizationUserRoleMemberEditRequest request) {
        String removeUserId = request.getUserIds().getFirst();
        checkMemberParam(removeUserId, request.getUserRoleId());
        //检查移除的是不是管理员
        if (Strings.CS.equals(request.getUserRoleId(), InternalUserRole.ORG_ADMIN.getValue())) {
            QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                    .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())
                            .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.ORG_ADMIN.getValue())));
            if (baseUserRoleRelationService.count(userRoleRelationQueryChain) == 0) {
                throw new CustomException(Translator.get("keep_at_least_one_administrator"));
            }
        }
        // 移除组织-用户组的成员, 若成员只存在该组织下唯一用户组, 则提示不能移除
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())
                        .and(USER_ROLE_RELATION.ROLE_ID.ne(request.getUserRoleId())));
        if (baseUserRoleRelationService.count(userRoleRelationQueryChain) == 0) {
            throw new CustomException(Translator.get("org_at_least_one_user_role_require"));
        }
        QueryChain<UserRoleRelation> example = QueryChain.of(UserRoleRelation.class)
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.getOrganizationId())
                        .and(USER_ROLE_RELATION.ROLE_ID.eq(request.getUserRoleId())));
        baseUserRoleRelationService.remove(example);
    }
}
