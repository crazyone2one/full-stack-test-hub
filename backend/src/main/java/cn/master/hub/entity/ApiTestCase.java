package cn.master.hub.entity;

import cn.master.hub.handler.validation.Created;
import cn.master.hub.handler.validation.Updated;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
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
import java.util.List;

/**
 * 接口用例 实体类。
 *
 * @author 11's papa
 * @since 2025-09-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "接口用例")
@Table("api_test_case")
public class ApiTestCase implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 接口用例pk
     */
    @Id
    @Schema(description = "接口用例pk", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_test_case.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{api_test_case.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "接口用例名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_test_case.name.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{api_test_case.name.length_range}", groups = {Created.class, Updated.class})
    private String name;

    @Schema(description = "用例等级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_test_case.priority.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{api_test_case.priority.length_range}", groups = {Created.class, Updated.class})
    private String priority;

    /**
     * 接口用例编号id
     */
    @Schema(description = "接口用例编号id")
    private Long num;

    /**
     * 标签
     */
    @Schema(description = "标签")
    @Column(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    @Schema(description = "用例状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_test_case.status.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 20, message = "{api_test_case.status.length_range}", groups = {Created.class, Updated.class})
    private String status;

    /**
     * 最新执行结果状态
     */
    @Schema(description = "最新执行结果状态")
    private String lastReportStatus;

    /**
     * 最后执行结果报告fk
     */
    @Schema(description = "最后执行结果报告fk")
    private String lastReportId;

    /**
     * 自定义排序
     */
    @Schema(description = "自定义排序")
    @NotNull(message = "{api_test_case.pos.not_blank}", groups = {Created.class})
    private Long pos;

    @Schema(description = "项目fk", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_test_case.project_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{api_test_case.project_id.length_range}", groups = {Created.class, Updated.class})
    private String projectId;

    /**
     * 接口fk
     */
    @Schema(description = "接口fk")
    private String apiDefinitionId;

    /**
     * 版本fk
     */
    @Schema(description = "版本fk")
    private String versionId;

    /**
     * 环境fk
     */
    @Schema(description = "环境fk")
    private String environmentId;

    /**
     * 请求内容
     */
    @Schema(description = "请求内容")
    private byte[] request;

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

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateUser;

    /**
     * 删除时间
     */
    @Schema(description = "删除时间")
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    @Schema(description = "删除人")
    private String deleteUser;

    /**
     * 删除标识
     */
    @Schema(description = "删除标识")
    private Boolean deleted;

}
