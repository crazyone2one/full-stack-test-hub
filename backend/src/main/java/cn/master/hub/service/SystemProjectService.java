package cn.master.hub.service;

import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.entity.SystemProject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 项目 服务层。
 *
 * @author 11's papa
 * @since 2025-09-01
 */
public interface SystemProjectService extends IService<SystemProject> {

    List<SystemProject> getUserProject(String organizationId, String currentUserName);

    ProjectDTO add(AddProjectRequest addProjectDTO, String createUser, String path, String module);

    Page<ProjectDTO> buildUserInfo(Page<ProjectDTO> page);
}
