package cn.master.hub.service.impl;

import cn.master.hub.constants.UserRoleEnum;
import cn.master.hub.constants.UserRoleType;
import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.project.ProjectUserDTO;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.dto.system.request.ProjectMemberRequest;
import cn.master.hub.entity.UserRole;
import cn.master.hub.service.ProjectMemberService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static cn.master.hub.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * @author Created by 11's papa on 2025/9/19
 */
@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {
    @Override
    public Page<ProjectUserDTO> pageMember(ProjectMemberRequest request) {
        return null;
    }

    @Override
    public List<UserExtendDTO> getMemberOption(String projectId, String keyword) {
        return List.of();
    }

    @Override
    public List<OptionDTO> getRoleOption(String projectId) {
        List<UserRole> userRoles = QueryChain.of(UserRole.class)
                .where(USER_ROLE.TYPE.eq(UserRoleType.PROJECT.name()).and(USER_ROLE.SCOPE_ID.in(Arrays.asList(projectId, UserRoleEnum.GLOBAL.toString()))))
                .list();
        return userRoles.stream().map(gp -> new OptionDTO(gp.getId(), gp.getName())).toList();
    }
}
