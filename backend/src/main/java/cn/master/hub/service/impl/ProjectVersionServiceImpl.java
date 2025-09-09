package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.ProjectVersion;
import cn.master.hub.mapper.ProjectVersionMapper;
import cn.master.hub.service.ProjectVersionService;
import org.springframework.stereotype.Service;

/**
 * 版本管理 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-01
 */
@Service
public class ProjectVersionServiceImpl extends ServiceImpl<ProjectVersionMapper, ProjectVersion>  implements ProjectVersionService{

}
