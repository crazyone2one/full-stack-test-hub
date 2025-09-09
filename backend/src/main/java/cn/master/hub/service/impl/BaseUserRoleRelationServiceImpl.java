package cn.master.hub.service.impl;

import cn.master.hub.entity.UserRoleRelation;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.mapper.UserRoleRelationMapper;
import cn.master.hub.service.BaseUserRoleRelationService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.hub.constants.InternalUserRole.ADMIN;
import static cn.master.hub.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.hub.handler.result.CommonResultCode.USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION;

/**
 * 用户组关系 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-09
 */
@Service
public class BaseUserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation> implements BaseUserRoleRelationService {

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

    private void checkAdminPermissionRemove(String userId, String roleId) {
        if (Strings.CS.equals(roleId, ADMIN.getValue()) && Strings.CS.equals(userId, ADMIN.getValue())) {
            throw new CustomException(USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION);
        }
    }

    private List<UserRoleRelation> getByRoleId(String roleId) {
        return queryChain().where(USER_ROLE_RELATION.ROLE_ID.eq(roleId)).list();
    }
}
