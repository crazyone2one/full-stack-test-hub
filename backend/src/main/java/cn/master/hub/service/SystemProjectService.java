package cn.master.hub.service;

import cn.master.hub.dto.UserDTO;
import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.request.ProjectRequest;
import cn.master.hub.dto.request.ProjectSwitchRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.SystemProjectRequest;
import cn.master.hub.dto.system.UpdateProjectRequest;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.dto.system.request.ProjectMemberRequest;
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

    List<ProjectDTO> buildUserInfo(List<ProjectDTO> projectList);

    ProjectDTO update(UpdateProjectRequest request, String updateUser, String path, String module);

    int delete(String id, String deleteUser);

    void enable(String id, String updateUser);

    void disable(String id, String updateUser);

    ProjectDTO getProjectById(String id);

    ProjectDTO update(ProjectRequest request, String currentUserName);

    UserDTO switchProject(ProjectSwitchRequest request, String currentUserId);

    Page<ProjectDTO> getProjectPage(SystemProjectRequest request);

    Page<UserExtendDTO> getProjectMember(ProjectMemberRequest request);
}
