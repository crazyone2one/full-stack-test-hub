package cn.master.hub.service;

import cn.master.hub.dto.request.AddProjectRequest;
import cn.master.hub.dto.response.ProjectDTO;
import cn.master.hub.dto.system.OrganizationProjectRequest;
import cn.master.hub.dto.system.UpdateProjectRequest;
import com.mybatisflex.core.paginate.Page;

/**
 * @author Created by 11's papa on 2025/9/10
 */
public interface OrganizationProjectService {
    ProjectDTO add(AddProjectRequest addProjectDTO, String createUser);

    Page<ProjectDTO> getProjectList(OrganizationProjectRequest request);

    ProjectDTO update(UpdateProjectRequest request, String updateUser);
}
