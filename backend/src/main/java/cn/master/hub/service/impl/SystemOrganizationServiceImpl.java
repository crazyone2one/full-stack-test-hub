package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.mapper.SystemOrganizationMapper;
import cn.master.hub.service.SystemOrganizationService;
import org.springframework.stereotype.Service;

/**
 * 组织 服务层实现。
 *
 * @author the2n
 * @since 2025-09-01
 */
@Service
public class SystemOrganizationServiceImpl extends ServiceImpl<SystemOrganizationMapper, SystemOrganization>  implements SystemOrganizationService{

}
