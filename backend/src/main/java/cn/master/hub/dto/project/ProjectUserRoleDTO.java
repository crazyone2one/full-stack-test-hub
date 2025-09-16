package cn.master.hub.dto.project;

import cn.master.hub.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author Created by 11's papa on 2025/9/16
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ProjectUserRoleDTO extends UserRole {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成员数目
     */
    @Schema(description = "成员数目")
    private Integer memberCount;
}
