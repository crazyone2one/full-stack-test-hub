package cn.master.hub.dto.system.request;

import cn.master.hub.dto.request.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Created by 11's papa on 2025/9/25
 */
@Getter
@Setter
public class QueryResourcePoolRequest extends BasePageRequest {
    @Schema(description =  "是否禁用")
    private Boolean enable;

    @Schema(description =  "是否删除")
    private Boolean deleted = false;

    @Schema(description =  "是否用于接口测试")
    private Boolean apiTest = false;

    @Schema(description =  "是否用于性能测试")
    private Boolean loadTest = false;

    @Schema(description =  "是否用于ui测试")
    private Boolean uiTest = false;
}
