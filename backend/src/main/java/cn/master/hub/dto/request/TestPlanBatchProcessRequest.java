package cn.master.hub.dto.request;

import cn.master.hub.dto.system.TableBatchProcessDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TestPlanBatchProcessRequest extends TableBatchProcessDTO {

    @Schema(description = "项目ID")
    @NotBlank(message = "{project.id.not_blank}")
    private String projectId;

    @Schema(description = "模块ID")
    private List<String> moduleIds;

    @Schema(description = "类型", allowableValues = {"ALL", "TEST_PLAN", "GROUP"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String type = "ALL";

}

