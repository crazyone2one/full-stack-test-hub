package cn.master.hub.service;

import cn.master.hub.dto.OptionDTO;
import cn.master.hub.dto.system.OrgUserExtend;
import cn.master.hub.dto.system.OrganizationDTO;
import cn.master.hub.dto.system.OrganizationProjectRequest;
import cn.master.hub.dto.system.UserExtendDTO;
import cn.master.hub.dto.system.request.*;
import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.entity.SystemProject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 组织 服务层。
 *
 * @author the2n
 * @since 2025-09-01
 */
public interface SystemOrganizationService extends IService<SystemOrganization> {

    List<OptionDTO> listAll();

    Map<String, Long> getTotal(String organizationId);

    void update(OrganizationDTO organizationDTO);

    Page<UserExtendDTO> getMemberPageBySystem(OrganizationProjectRequest request);
    Page<OrgUserExtend> getMemberPageByOrg(OrganizationProjectRequest request);

    void addMemberBySystem(OrganizationMemberRequest request, String currentUserName);

    void addMemberBySystem(OrganizationMemberBatchRequest batchRequest, String createUserId);
    void addMemberByOrg(OrganizationMemberExtendRequest organizationMemberExtendRequest, String createUserId);

    void removeMember(String organizationId, String userId, String currentUserName);

    void addMemberRole(OrganizationMemberExtendRequest request, String currentUserName);

    void updateMember(OrganizationMemberUpdateRequest organizationMemberExtendRequest, String currentUserName, String path, String module);

    void addMemberToProject(OrgMemberExtendProjectRequest orgMemberExtendProjectRequest, String currentUserName);

    List<OptionDTO> getUserRoleList(String organizationId);

    Page<UserExtendDTO> getMemberList(MemberRequest request);

    List<UserExtendDTO> getMemberOption(String sourceId, String keyword);

    Map<SystemOrganization, List<SystemProject>> getOrgProjectMap();
}
