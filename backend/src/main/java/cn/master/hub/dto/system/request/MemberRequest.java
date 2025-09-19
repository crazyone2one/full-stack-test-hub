package cn.master.hub.dto.system.request;

import cn.master.hub.dto.request.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 2025/9/19
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MemberRequest extends BasePageRequest {

    @Schema(description =  "组织ID或项目ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private String sourceId;
}
