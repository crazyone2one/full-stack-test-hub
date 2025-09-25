package cn.master.hub.service;

import cn.master.hub.dto.response.UserTableResponse;
import cn.master.hub.dto.system.UserExcludeOptionDTO;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRoleRelation;
import com.mybatisflex.core.service.IService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

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

    void updateUserSystemGlobalRole(@Valid SystemUser user, @Valid @NotEmpty String operator, @Valid @NotEmpty List<String> roleList);

    Map<String, UserTableResponse> selectGlobalUserRoleAndOrganization(List<String> userIdList);

    void deleteByUserIdList(List<String> userIdList);

    void checkExist(UserRoleRelation userRoleRelation);

    UserRole getUserRole(String id);

    void delete(String id);

    List<UserExcludeOptionDTO> getExcludeSelectOptionWithLimit(String roleId, String keyword);
}
