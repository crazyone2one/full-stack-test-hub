package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.ProjectApplication;
import cn.master.hub.mapper.ProjectApplicationMapper;
import cn.master.hub.service.ProjectApplicationService;
import org.springframework.stereotype.Service;

/**
 * 项目应用 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-01
 */
@Service
public class ProjectApplicationServiceImpl extends ServiceImpl<ProjectApplicationMapper, ProjectApplication>  implements ProjectApplicationService{

}
