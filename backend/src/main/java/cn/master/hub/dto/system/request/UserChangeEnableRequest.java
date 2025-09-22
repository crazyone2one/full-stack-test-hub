package cn.master.hub.dto.system.request;

import cn.master.hub.dto.system.TableBatchProcessDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Created by 11's papa on 2025/9/22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserChangeEnableRequest extends TableBatchProcessDTO {
    @Schema(description = "禁用/启用", requiredMode = Schema.RequiredMode.REQUIRED)
    boolean enable;
}
