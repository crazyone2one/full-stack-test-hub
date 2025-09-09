package cn.master.hub.service;

import cn.master.hub.dto.UserCreateInfo;
import cn.master.hub.dto.request.BasePageRequest;
import cn.master.hub.dto.request.UserBatchCreateRequest;
import cn.master.hub.dto.response.UserBatchCreateResponse;
import cn.master.hub.entity.SystemUser;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

/**
 * 用户 服务层。
 *
 * @author the2n
 * @since 2025-08-29
 */
public interface SystemUserService extends IService<SystemUser> {

    Page<SystemUser> getUserPage(BasePageRequest request);

    UserBatchCreateResponse addUser(UserBatchCreateRequest userCreateDTO, String name, String currentUserName);

    String addUser(UserCreateInfo userCreateDTO, String name, String currentUserName);

    void updateUser(SystemUser user);

    boolean isSuperUser(String id);
}
