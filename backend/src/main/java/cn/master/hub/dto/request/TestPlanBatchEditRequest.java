package cn.master.hub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/9/28
 */
@Data
public class TestPlanBatchEditRequest extends TestPlanBatchProcessRequest {

    @Schema(description = "是否追加")
    private boolean append = true;

    @Schema(description = "是否清空")
    private boolean clear;

    @Schema(description = "标签")
    private List<String> tags;

    @Schema(description = "本次编辑的字段", allowableValues = {"TAGS"})
    private String editColumn;
}
