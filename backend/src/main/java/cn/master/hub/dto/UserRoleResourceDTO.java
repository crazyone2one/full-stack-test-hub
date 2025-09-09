package cn.master.hub.dto;

import cn.master.hub.entity.UserRole;
import cn.master.hub.entity.UserRolePermission;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/9
 */
@Data
public class UserRoleResourceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UserRoleResource resource;
    private List<UserRolePermission> permissions;
    private String type;

    private UserRole userRole;
    private List<UserRolePermission> userRolePermissions;
}
