package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.mapper.SystemUserMapper;
import cn.master.hub.service.SystemUserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author the2n
 * @since 2025-08-29
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser>  implements SystemUserService{

}
