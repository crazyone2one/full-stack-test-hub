package cn.master.hub.entity;

import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 版本管理 实体类。
 *
 * @author 11's papa
 * @since 2025-09-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "版本管理")
@Table("project_version")
public class ProjectVersion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 版本ID
     */
    @Id
    @Schema(description =  "版本ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{project_version.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{project_version.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description =  "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{project_version.project_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{project_version.project_id.length_range}", groups = {Created.class, Updated.class})
    private String projectId;

    @Schema(description =  "版本名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{project_version.name.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{project_version.name.length_range}", groups = {Created.class, Updated.class})
    private String name;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 是否是最新版
     */
    @Schema(description = "是否是最新版")
    private Boolean latest;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

}
