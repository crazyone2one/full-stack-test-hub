package cn.master.hub.dto.request.project;

import cn.master.hub.dto.request.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 2025/9/16
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ProjectUserRoleRequest extends BasePageRequest {
    @Schema(description = "项目ID")
    private String projectId;
}
