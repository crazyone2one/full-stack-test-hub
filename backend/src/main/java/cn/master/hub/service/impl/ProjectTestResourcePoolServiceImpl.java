package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.ProjectTestResourcePool;
import cn.master.hub.mapper.ProjectTestResourcePoolMapper;
import cn.master.hub.service.ProjectTestResourcePoolService;
import org.springframework.stereotype.Service;

/**
 * 项目与资源池关系表 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-10
 */
@Service
public class ProjectTestResourcePoolServiceImpl extends ServiceImpl<ProjectTestResourcePoolMapper, ProjectTestResourcePool>  implements ProjectTestResourcePoolService{

}
