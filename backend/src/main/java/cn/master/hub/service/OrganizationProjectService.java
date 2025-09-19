package cn.master.hub.service;

import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.request.ProjectAddMemberRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.OrganizationProjectRequest;
import cn.master.hub.dto.system.UpdateProjectRequest;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.dto.system.request.ProjectUserRequest;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/10
 */
public interface OrganizationProjectService {
    ProjectDTO add(AddProjectRequest addProjectDTO, String createUser);

    Page<ProjectDTO> getProjectList(OrganizationProjectRequest request);

    ProjectDTO update(UpdateProjectRequest request, String updateUser);

    List<UserExtendDTO> getUserAdminList(String organizationId, String keyword);

    int delete(String id, String deleteUser);

    void enable(String id, String updateUser);

    void disable(String id, String updateUser);

    Page<UserExtendDTO> getMemberList(ProjectUserRequest request);

    void orgAddProjectMember(ProjectAddMemberRequest request, String currentUserName);
}
