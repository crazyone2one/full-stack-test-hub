package cn.master.hub.dto.system;

import cn.master.hub.dto.request.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 2025/9/17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemProjectRequest extends BasePageRequest {
    @Schema(description =  "组织ID")
    private String organizationId;
    @Schema(description =  "项目ID")
    private String projectId;
}
