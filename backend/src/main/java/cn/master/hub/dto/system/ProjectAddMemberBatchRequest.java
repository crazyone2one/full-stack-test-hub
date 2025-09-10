package cn.master.hub.dto.system;

import cn.master.hub.dto.request.ProjectAddMemberRequest;
import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectAddMemberBatchRequest extends ProjectAddMemberRequest {
    @Schema(description = "项目ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<
            @NotBlank(message = "{project.id.not_blank}", groups = {Created.class, Updated.class})
                    String> projectIds;
}
