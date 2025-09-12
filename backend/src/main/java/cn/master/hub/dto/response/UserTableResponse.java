package cn.master.hub.dto.response;

import cn.master.hub.entity.SystemOrganization;
import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserTableResponse extends SystemUser {
    @Schema(description =  "用户所属组织")
    private List<SystemOrganization> organizationList = new ArrayList<>();
    @Schema(description = "用户所属用户组")
    private List<UserRole> userRoleList = new ArrayList<>();
    public void setOrganization(SystemOrganization organization) {
        if (!organizationList.contains(organization)) {
            organizationList.add(organization);
        }
    }

    public void setUserRole(UserRole userRole) {
        if (!userRoleList.contains(userRole)) {
            userRoleList.add(userRole);
        }
    }
}
