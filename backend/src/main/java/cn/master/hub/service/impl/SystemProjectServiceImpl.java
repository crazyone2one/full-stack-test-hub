package cn.master.hub.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.hub.entity.SystemProject;
import cn.master.hub.mapper.SystemProjectMapper;
import cn.master.hub.service.SystemProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.hub.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 2025-09-01
 */
@Service
public class SystemProjectServiceImpl extends ServiceImpl<SystemProjectMapper, SystemProject> implements SystemProjectService {

    @Override
    public List<SystemProject> getUserProject(String organizationId, String currentUserName) {
        return queryChain().where(SYSTEM_PROJECT.ORGANIZATION_ID.eq(organizationId)
                .and(SYSTEM_PROJECT.ENABLE.eq(true))).list();
    }
}
