package cn.master.hub.entity;

import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试计划模块 实体类。
 *
 * @author 11's papa
 * @since 2025-09-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "测试计划模块")
@Table("test_plan_module")
public class TestPlanModule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{test_plan_module.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{test_plan_module.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{test_plan_module.project_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{test_plan_module.project_id.length_range}", groups = {Created.class, Updated.class})
    private String projectId;

    @Schema(description = "模块名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{test_plan_module.name.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 64, message = "{test_plan_module.name.length_range}", groups = {Created.class, Updated.class})
    private String name;

    @Schema(description = "模块ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{test_plan_module.parent_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{test_plan_module.parent_id.length_range}", groups = {Created.class, Updated.class})
    private String parentId;

    @Schema(description = "排序标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{test_plan_module.pos.not_blank}", groups = {Created.class})
    private Long pos;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateUser;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

}
