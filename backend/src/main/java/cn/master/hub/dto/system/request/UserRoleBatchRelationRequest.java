package cn.master.hub.dto.system.request;

import cn.master.hub.dto.system.TableBatchProcessDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/23
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class UserRoleBatchRelationRequest extends TableBatchProcessDTO {
    /**
     * 权限ID集合
     */
    @Schema(description = "权限ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "{organization.id.not_blank}")
    private List<String> roleIds;
}
