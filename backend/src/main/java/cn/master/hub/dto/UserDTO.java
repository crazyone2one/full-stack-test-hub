package cn.master.hub.dto;

import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRoleRelation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/9
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends SystemUser {
    private List<UserRole> userRoles = new ArrayList<>();
    private List<UserRoleRelation> userRoleRelations = new ArrayList<>();
    private List<UserRoleResourceDTO> userRolePermissions = new ArrayList<>();
}
