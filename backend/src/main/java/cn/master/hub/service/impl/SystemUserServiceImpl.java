package cn.master.hub.service.impl;

import cn.master.hub.dto.request.BasePageRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.mapper.SystemUserMapper;
import cn.master.hub.service.SystemUserService;
import org.springframework.stereotype.Service;

import static cn.master.hub.entity.table.SystemUserTableDef.SYSTEM_USER;

/**
 * 用户 服务层实现。
 *
 * @author the2n
 * @since 2025-08-29
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser>  implements SystemUserService{

    @Override
    public Page<SystemUser> getUserPage(BasePageRequest request) {
        return queryChain()
                .where(SYSTEM_USER.NAME.like(request.getKeyword())
                        .or(SYSTEM_USER.EMAIL.like(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.like(request.getKeyword())))
                .page(new Page<>(request.getPage(), request.getPageSize()));
    }
}
