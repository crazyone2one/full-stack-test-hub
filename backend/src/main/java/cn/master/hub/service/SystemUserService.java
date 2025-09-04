package cn.master.hub.service;

import cn.master.hub.dto.request.BasePageRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.hub.entity.SystemUser;

/**
 * 用户 服务层。
 *
 * @author the2n
 * @since 2025-08-29
 */
public interface SystemUserService extends IService<SystemUser> {

    Page<SystemUser> getUserPage(BasePageRequest request);
}
