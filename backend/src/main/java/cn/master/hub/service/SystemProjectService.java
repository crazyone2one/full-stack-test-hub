package cn.master.hub.service;

import com.mybatisflex.core.service.IService;
import cn.master.hub.entity.SystemProject;

import java.util.List;

/**
 * 项目 服务层。
 *
 * @author 11's papa
 * @since 2025-09-01
 */
public interface SystemProjectService extends IService<SystemProject> {

    List<SystemProject> getUserProject(String organizationId, String currentUserName);
}
