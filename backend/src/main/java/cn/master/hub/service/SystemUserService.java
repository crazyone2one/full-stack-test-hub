package cn.master.hub.service;

import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.dto.request.BasePageRequest;
import cn.master.hub.dto.request.UserBatchCreateRequest;
import cn.master.hub.dto.request.UserEditRequest;
import cn.master.hub.dto.response.UserBatchCreateResponse;
import cn.master.hub.dto.response.UserTableResponse;
import cn.master.hub.dto.system.TableBatchProcessDTO;
import cn.master.hub.dto.system.TableBatchProcessResponse;
import cn.master.hub.dto.system.UserSelectOption;
import cn.master.hub.dto.system.request.UserChangeEnableRequest;
import cn.master.hub.dto.system.request.UserRoleBatchRelationRequest;
import cn.master.hub.entity.SystemUser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author the2n
 * @since 2025-08-29
 */
public interface SystemUserService extends IService<SystemUser> {

    Page<UserTableResponse> getUserPage(BasePageRequest request);

    UserBatchCreateResponse addUser(UserBatchCreateRequest userCreateDTO, String name, String currentUserName);

    String addUser(UserCreateInfo userCreateDTO, String name, String currentUserName);

    void updateUser(SystemUser user);

    boolean isSuperUser(String id);

    List<UserSelectOption> getGlobalSystemRoleList();

    UserEditRequest updateUser(UserEditRequest request, String operator);

    TableBatchProcessResponse deleteUser(TableBatchProcessDTO request, String currentUserName, String username);

    TableBatchProcessResponse updateUserEnable(UserChangeEnableRequest request, String currentUserName, String username);

    TableBatchProcessResponse addMemberToOrg(UserRoleBatchRelationRequest userRoleBatchRelationRequest);
}
