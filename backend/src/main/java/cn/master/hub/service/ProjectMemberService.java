package cn.master.hub.service;

import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.project.ProjectUserDTO;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.dto.system.request.ProjectMemberRequest;
import com.mybatisflex.core.paginate.Page;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/19
 */
public interface ProjectMemberService {
    Page<ProjectUserDTO> pageMember(ProjectMemberRequest request);

    List<UserExtendDTO> getMemberOption(String projectId, String keyword);

    List<OptionDTO> getRoleOption(String projectId);
}
