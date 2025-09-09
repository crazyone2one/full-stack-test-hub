package cn.master.hub.service;

import cn.master.hub.entity.UserRoleRelation;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户组关系 服务层。
 *
 * @author 11's papa
 * @since 2025-09-09
 */
public interface BaseUserRoleRelationService extends IService<UserRoleRelation> {

    void deleteByRoleId(String id);

    List<String> getUserIdByRoleId(String roleId);

    List<UserRoleRelation> getUserIdAndSourceIdByUserIds(List<String> userIds);
}
