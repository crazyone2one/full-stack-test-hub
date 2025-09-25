package cn.master.hub.dto.system.request;

import cn.master.hub.dto.request.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 2025/9/24
 */
@Getter
@Setter
public class GlobalUserRoleRelationQueryRequest extends BasePageRequest {
    @NotBlank
    @Schema(description =  "用户组ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleId;
}
