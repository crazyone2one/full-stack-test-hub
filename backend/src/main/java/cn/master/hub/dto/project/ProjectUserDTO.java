package cn.master.hub.dto.project;

import cn.master.hub.entity.SystemUser;
import cn.master.hub.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/19
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ProjectUserDTO extends SystemUser {
    @Schema(description =  "用户组集合")
    private List<UserRole> userRoles;
}
