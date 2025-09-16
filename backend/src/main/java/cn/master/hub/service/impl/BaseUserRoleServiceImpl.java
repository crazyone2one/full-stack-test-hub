package cn.master.hub.service.impl;

import cn.master.hub.constants.InternalUserRole;
import cn.master.hub.constants.UserRoleEnum;
import cn.master.hub.constants.UserRoleScope;
import cn.master.hub.constants.UserRoleType;
import cn.master.hub.dto.Permission;
import cn.master.hub.dto.PermissionDefinitionItem;
import cn.master.hub.dto.request.PermissionSettingUpdateRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRolePermission;
import cn.master.hub.entity.UserRoleRelation;
import cn.master.hub.handler.PermissionCache;
import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.mapper.UserRoleMapper;
import cn.master.hub.service.BaseUserRolePermissionService;
import cn.master.hub.service.BaseUserRoleRelationService;
import cn.master.hub.service.BaseUserRoleService;
import cn.master.hub.util.JacksonUtils;
import cn.master.hub.util.ServiceUtils;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.hub.entity.table.UserRoleTableDef.USER_ROLE;
import static cn.master.hub.handler.result.CommonResultCode.ADMIN_USER_ROLE_PERMISSION;
import static cn.master.hub.handler.result.CommonResultCode.INTERNAL_USER_ROLE_PERMISSION;
import static cn.master.hub.handler.result.ResultCode.NO_GLOBAL_USER_ROLE_PERMISSION;

/**
 * 用户组 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-09
 */
@Service
@RequiredArgsConstructor
public class BaseUserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements BaseUserRoleService {
    private final BaseUserRolePermissionService baseUserRolePermissionService;
    private final BaseUserRoleRelationService baseUserRoleRelationService;
    private final PermissionCache permissionCache;

    @Override
    public List<UserRole> list() {
        List<UserRole> userRoles = queryChain().where(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL)).list();
        // 先按照类型排序，再按照创建时间排序
        userRoles.sort(Comparator.comparingInt(this::getTypeOrder)
                .thenComparingInt(item -> getInternal(item.getInternal()))
                .thenComparing(UserRole::getCreateTime));
        return userRoles;
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(UserRole userRole) {
        Set<String> permissionIds = baseUserRolePermissionService.getPermissionIdSetByRoleId(userRole.getId());
        List<PermissionDefinitionItem> permissionDefinition = permissionCache.getPermissionDefinition();
        permissionDefinition = JacksonUtils.parseArray(JacksonUtils.toJSONString(permissionDefinition), PermissionDefinitionItem.class);
        permissionDefinition = permissionDefinition.stream()
                .filter(item -> Strings.CS.equals(item.getType(), userRole.getType()) || Strings.CS.equals(userRole.getId(), InternalUserRole.ADMIN.getValue()))
                .sorted(Comparator.comparing(PermissionDefinitionItem::getOrder))
                .collect(Collectors.toList());
        for (PermissionDefinitionItem firstLevel : permissionDefinition) {
            List<PermissionDefinitionItem> children = firstLevel.getChildren();
            boolean allCheck = true;
            firstLevel.setName(Translator.get(firstLevel.getName()));
            for (PermissionDefinitionItem secondLevel : children) {
                List<Permission> permissions = secondLevel.getPermissions();
                secondLevel.setName(Translator.get(secondLevel.getName()));
                if (CollectionUtils.isEmpty(permissions)) {
                    continue;
                }
                boolean secondAllCheck = true;
                for (Permission p : permissions) {
                    if (StringUtils.isNotBlank(p.getName())) {
                        // 有 name 字段翻译 name 字段
                        p.setName(Translator.get(p.getName()));
                    } else {
                        p.setName(translateDefaultPermissionName(p));
                    }
                    // 管理员默认勾选全部二级权限位
                    if (permissionIds.contains(p.getId()) || Strings.CS.equals(userRole.getId(), InternalUserRole.ADMIN.getValue())) {
                        p.setEnable(true);
                    } else {
                        // 如果权限有未勾选，则二级菜单设置为未勾选
                        p.setEnable(false);
                        secondAllCheck = false;
                    }
                }
                secondLevel.setEnable(secondAllCheck);
                if (!secondAllCheck) {
                    // 如果二级菜单有未勾选，则一级菜单设置为未勾选
                    allCheck = false;
                }
            }
            firstLevel.setEnable(allCheck);
        }
        return permissionDefinition;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserRole add(UserRole userRole) {
        mapper.insert(userRole);
        if (Strings.CS.equals(userRole.getType(), UserRoleType.PROJECT.name())) {
            // 项目级别用户组, 初始化基本信息权限
            UserRolePermission initPermission = new UserRolePermission();
            initPermission.setRoleId(userRole.getId());
            initPermission.setPermissionId("PROJECT_BASE_INFO:READ");
            baseUserRolePermissionService.save(initPermission);
        }
        return userRole;
    }

    @Override
    public UserRole update(UserRole userRole) {
        mapper.update(userRole);
        return userRole;
    }

    @Override
    public void delete(UserRole userRole, String defaultRoleId, String currentUserId, String orgId) {
        String id = userRole.getId();
        checkInternalUserRole(userRole);
        baseUserRolePermissionService.deleteByRoleId(id);
        mapper.deleteById(id);
        // 检查是否只有一个用户组，如果是则添加系统成员等默认用户组
        checkOneLimitRole(id, defaultRoleId, currentUserId, orgId);

        // 删除用户组与用户的关联关系
        baseUserRoleRelationService.deleteByRoleId(id);
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        baseUserRolePermissionService.updatePermissionSetting(request);
    }

    @Override
    public UserRole getWithCheck(String id) {
        return checkResourceExist(mapper.selectOneById(id));
    }

    @Override
    public UserRole checkResourceExist(UserRole userRole) {
        return ServiceUtils.checkResourceExist(userRole, "permission.system_user_role.name");
    }

    @Override
    public void checkAdminUserRole(UserRole userRole) {
        if (Strings.CS.equalsAny(userRole.getId(), InternalUserRole.ADMIN.getValue(),
                InternalUserRole.ORG_ADMIN.getValue(), InternalUserRole.PROJECT_ADMIN.getValue())) {
            throw new CustomException(ADMIN_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public void checkGlobalUserRole(UserRole userRole) {
        if (Strings.CS.equals(userRole.getScopeId(), UserRoleEnum.GLOBAL.toString())) {
            throw new CustomException(NO_GLOBAL_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public UserRole get(String id) {
        UserRole userRole = mapper.selectOneById(id);
        if (userRole == null) {
            throw new CustomException(Translator.get("user_role_not_exist"));
        }
        return userRole;
    }

    private void checkOneLimitRole(String roleId, String defaultRoleId, String currentUserId, String orgId) {
        List<String> userIds = baseUserRoleRelationService.getUserIdByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        // 查询用户列表与所有用户组的关联关系，并分组（UserRoleRelation 中只有 userId 和 sourceId）
        Map<String, List<UserRoleRelation>> userRoleRelationMap = baseUserRoleRelationService
                .getUserIdAndSourceIdByUserIds(userIds)
                .stream()
                .collect(Collectors.groupingBy(i -> i.getUserId() + i.getSourceId()));

        List<UserRoleRelation> addRelations = new ArrayList<>();
        userRoleRelationMap.forEach((groupId, relations) -> {
            // 如果当前用户组只有一个用户，并且就是要删除的用户组，则添加组织成员等默认用户组
            if (relations.size() == 1 && Strings.CS.equals(relations.getFirst().getRoleId(), roleId)) {
                UserRoleRelation relation = new UserRoleRelation();
                relation.setUserId(relations.getFirst().getUserId());
                relation.setSourceId(relations.getFirst().getSourceId());
                relation.setRoleId(defaultRoleId);
                relation.setCreateUser(currentUserId);
                relation.setOrganizationId(orgId);
                addRelations.add(relation);
            }
        });

        baseUserRoleRelationService.saveBatch(addRelations);
    }

    protected void checkInternalUserRole(UserRole userRole) {
        if (BooleanUtils.isTrue(userRole.getInternal())) {
            throw new CustomException(INTERNAL_USER_ROLE_PERMISSION);
        }
    }

    private String translateDefaultPermissionName(Permission p) {
//        if (StringUtils.isNotBlank(p.getName())) {
//            p.getName();
//        }
        String[] idSplit = p.getId().split(":");
        String permissionKey = idSplit[idSplit.length - 1];
        Map<String, String> translationMap = new HashMap<>() {{
            put("READ", "permission.read");
            put("READ+ADD", "permission.add");
            put("READ+UPDATE", "permission.edit");
            put("READ+DELETE", "permission.delete");
            put("READ+IMPORT", "permission.import");
            put("READ+RECOVER", "permission.recover");
            put("READ+EXPORT", "permission.export");
            put("READ+EXECUTE", "permission.execute");
            put("READ+DEBUG", "permission.debug");
        }};
        return Translator.get(translationMap.get(permissionKey));
    }

    private int getInternal(Boolean internal) {
        return BooleanUtils.isTrue(internal) ? 0 : 1;
    }

    private int getTypeOrder(UserRole o) {
        Map<String, Integer> typeOrderMap = new HashMap<>(3) {{
            put(UserRoleType.SYSTEM.name(), 1);
            put(UserRoleType.ORGANIZATION.name(), 2);
            put(UserRoleType.PROJECT.name(), 3);
        }};
        return typeOrderMap.getOrDefault(o.getType(), 0);
    }
}
